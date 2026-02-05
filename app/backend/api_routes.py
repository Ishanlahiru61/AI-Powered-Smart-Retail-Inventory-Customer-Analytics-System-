from fastapi import APIRouter, HTTPException, Query
import pandas as pd
import numpy as np
from app.backend.database import get_collection
from pydantic import BaseModel
from typing import List, Optional
from mlxtend.frequent_patterns import apriori, association_rules
from datetime import datetime, date
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler

router = APIRouter()



class RecommendationRequest(BaseModel):
    selected_items: List[str]
    start_date: Optional[str] = None
    end_date: Optional[str] = None


def filter_by_date(df, start_date: str = None, end_date: str = None):
    """Filters dataframe by date or defaults to a 30-day window."""
    if "timestamp" not in df.columns or df.empty:
        return df
    
    df['date_dt'] = pd.to_datetime(df['timestamp']).dt.date
    today = date.today()
    
  
    if end_date:
        end = datetime.strptime(end_date, "%Y-%m-%d").date()
    else:
        end = today
        
    
    if start_date:
        start = datetime.strptime(start_date, "%Y-%m-%d").date()
    else:
        start = end - pd.Timedelta(days=30)
    
    return df[(df['date_dt'] >= start) & (df['date_dt'] <= end)]


@router.get("/transactions")
def get_transactions(
    start_date: str = Query(None),
    end_date: str = Query(None)
):
    try:
        collection = get_collection("grocery_products")
        data = list(collection.find({}))
        if not data:
            return []
        
        df = pd.DataFrame(data)
        if "_id" in df.columns: 
            df.drop(columns=["_id"], inplace=True)
        
        df = filter_by_date(df, start_date, end_date)
        
        if "timestamp" in df.columns: 
            df["timestamp"] = df["timestamp"].astype(str)
        if "date_dt" in df.columns:
            df.drop(columns=["date_dt"], inplace=True)
            
        return df.to_dict(orient="records")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/clusters/products")
def get_product_clusters(
    start_date: str = Query(None),
    end_date: str = Query(None)
):
    try:
        data = get_transactions(start_date, end_date)
        if not data:
            return {"data": [], "message": "Insufficient data"}
        
        df = pd.DataFrame(data)
        metrics = df.groupby('product_name').agg({
            'quantity': 'sum',
            'transaction_id': 'nunique'
        }).reset_index()
        metrics.columns = ['product_name', 'total_volume', 'frequency']

        if len(metrics) < 1:
            return {"data": [], "message": "No products in range"}

        
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(metrics[['total_volume', 'frequency']])
        
        k = min(3, len(metrics))
        kmeans = KMeans(n_clusters=k, random_state=42, n_init=10)
        metrics['cluster'] = kmeans.fit_predict(X_scaled)

        
        avg_vol = metrics.groupby('cluster')['total_volume'].mean().sort_values().index
        labels = ["Low Demand", "Medium Demand", "High Demand"]
        mapping = {avg_vol[i]: labels[i] for i in range(len(avg_vol))}
        metrics['segment'] = metrics['cluster'].map(mapping)

        return {"data": metrics[['product_name', 'total_volume', 'frequency', 'segment']].to_dict(orient="records")}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/recommendations")
def get_recommendations(request: RecommendationRequest):
    try:
        data = get_transactions(request.start_date, request.end_date)
        if not data:
            return {"data": [], "message": "No data found"}

        df = pd.DataFrame(data)
        
        
        basket = pd.crosstab(df['transaction_id'], df['product_name'])
        basket_sets = basket.map(lambda x: True if x > 0 else False)
        
        freq_itemsets = apriori(basket_sets, min_support=0.01, use_colnames=True)
        if freq_itemsets.empty:
            return {"data": [], "message": "No patterns found"}
            
        rules = association_rules(freq_itemsets, metric="confidence", min_threshold=0.1)

        selection = set(request.selected_items)
        filtered = rules[rules['antecedents'] == frozenset(selection)].copy()

        if filtered.empty:
            return {"data": [], "message": "No recommendations available"}

       
        filtered['item'] = filtered['consequents'].apply(lambda x: list(x)[0])
        filtered['percentage'] = (filtered['confidence'] * 100).round(2)
        
        
        final = filtered[~filtered['item'].isin(selection)]
        
        
        cleaned_results = final.groupby('item')['percentage'].max().reset_index()
        cleaned_results = cleaned_results.sort_values(by='percentage', ascending=False)
        
        return {"data": cleaned_results.to_dict(orient="records")}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/inventory/anomalies")
def detect_anomalies(
    start_date: str = Query(None),
    end_date: str = Query(None)
):
    try:
        data = get_transactions(start_date, end_date)
        if not data: return {"anomalies": []}

        df = pd.DataFrame(data)
        df['date'] = pd.to_datetime(df['timestamp']).dt.date
        daily = df.groupby(['product_name', 'date'])['quantity'].sum().reset_index()
        
        results = []
        for product in daily['product_name'].unique():
            subset = daily[daily['product_name'] == product]['quantity']
            
            
            if len(subset) < 4: continue 

            q1, q3 = np.percentile(subset, [25, 75])
            iqr = q3 - q1
            upper = q3 + (1.5 * iqr)
            lower = q1 - (1.5 * iqr)

            outliers = daily[(daily['product_name'] == product) & 
                             ((daily['quantity'] > upper) | (daily['quantity'] < lower))]

            for _, row in outliers.iterrows():
                results.append({
                    "product": row['product_name'],
                    "date": str(row['date']),
                    "quantity": int(row['quantity']),
                    "type": "Spike 📈" if row['quantity'] > upper else "Drop 📉",
                    "reason": "Volume outside normal statistical range"
                })
        return {"anomalies": results}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))