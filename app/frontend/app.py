import streamlit as st
from datetime import date
import pandas as pd


try:
    from app.frontend.api import (
        get_transactions,
        get_clusters,
        get_anomalies,
        get_recommendations
    )
    from app.frontend.charts import cluster_bar, cross_sell_donut
    from app.frontend.styles import load_styles
except ModuleNotFoundError:
    from api import (
        get_transactions,
        get_clusters,
        get_anomalies,
        get_recommendations
    )
    from charts import cluster_bar, cross_sell_donut
    from styles import load_styles



st.set_page_config(page_title="Executive AI Engine", layout="wide")
st.markdown(load_styles(), unsafe_allow_html=True)


with st.sidebar:
    st.markdown("##  Control Center")
    start = st.date_input("Analysis Start", date(2026, 1, 1))
    end = st.date_input("Analysis End", date(2026, 1, 28))
    st.divider()
    st.info("AI Models are processing real-time MongoDB streams.")
   

st.title("Smart Grocery Dashboard")


transactions = get_transactions(start, end)
anomalies = get_anomalies(start, end)
clusters = get_clusters(start, end)


k1, k2, k3, k4 = st.columns(4)
k1.metric("Total Volume", f"{len(transactions):,}")
k2.metric(
    "Anomaly Rate",
    f"{(len(anomalies) / len(transactions) * 100 if len(transactions) > 0 else 0):.1f}%"
)
k3.metric("System Load", "Stable")
k4.metric(
    "Market Sentiment",
    "Bullish" if len(transactions) > 100 else "Neutral"
)

st.divider()


left, right = st.columns([2, 1])

with left:
    st.markdown("###  Demand Intelligence")
    st.plotly_chart(cluster_bar(clusters), use_container_width=True)

    st.markdown("###  Smart Cross-Sell Logic")
    if not transactions.empty:
        p_name = st.selectbox(
            "Focus Product",
            sorted(transactions["product_name"].unique())
        )

        recs = get_recommendations([p_name], start, end)

        c_left, c_right = st.columns([1.5, 1])

        with c_left:
            st.plotly_chart(
                cross_sell_donut(recs),
                use_container_width=True
            )

        with c_right:
            if not recs.empty:
                top = recs.iloc[0]
                true_pct = (top["percentage"] / recs["percentage"].sum()) * 100

                st.success(
                    f"Users buying **{p_name}** are "
                    f"**{true_pct:.1f}%** likely to buy **{top['item']}**."
                )
                

with right:
    st.markdown("###  Incident Log")
    if anomalies.empty:
        st.success(" All systems nominal.")
    else:
        for _, row in anomalies.iterrows():
            st.markdown(
                f"""
                <div style="border-left: 5px solid #F43F5E;
                            padding: 10px 15px;
                            background: rgba(244, 63, 94, 0.05);
                            border-radius: 0 10px 10px 0;
                            margin-bottom: 10px;">
                    <small style='color: #64748B;'>{row['date']}</small><br>
                    <strong style='color: #1E293B;'>{row['product']}</strong><br>
                    <span style='color: #F43F5E;'>{row['type']} Spike</span>
                </div>
                """,
                unsafe_allow_html=True
            )


with st.expander("System Audit Logs"):
    st.dataframe(transactions, use_container_width=True)
