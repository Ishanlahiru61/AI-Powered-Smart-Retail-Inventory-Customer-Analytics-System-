import requests
import pandas as pd

BASE_URL = "http://localhost:8000/api"

def get_transactions(start, end):
    try:
        r = requests.get(f"{BASE_URL}/transactions", params={"start_date": start, "end_date": end})
        return pd.DataFrame(r.json())
    except:
        return pd.DataFrame()

def get_clusters(start, end):
    try:
        r = requests.get(f"{BASE_URL}/clusters/products", params={"start_date": start, "end_date": end})
        return pd.DataFrame(r.json().get("data", []))
    except:
        return pd.DataFrame()

def get_anomalies(start, end):
    try:
        r = requests.get(f"{BASE_URL}/inventory/anomalies", params={"start_date": start, "end_date": end})
        return pd.DataFrame(r.json().get("anomalies", []))
    except:
        return pd.DataFrame()

def get_recommendations(items, start, end):
    try:
        # Match the backend @router.post("/recommendations")
        payload = {
            "selected_items": items,
            "start_date": str(start),
            "end_date": str(end)
        }
        r = requests.post(f"{BASE_URL}/recommendations", json=payload)
        return pd.DataFrame(r.json().get("data", []))
    except:
        return pd.DataFrame()