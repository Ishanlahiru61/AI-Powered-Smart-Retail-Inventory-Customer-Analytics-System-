import plotly.express as px
import plotly.graph_objects as go


def cluster_bar(df):
    if df.empty:
        return go.Figure()

    COLORS = {
        "High Demand": "#10B981",
        "Medium Demand": "#6366F1",
        "Low Demand": "#64748B"
    }

    fig = px.bar(
        df,
        x="product_name",
        y="total_volume",
        color="segment",
        color_discrete_map=COLORS,
        template="plotly_white"
    )

    fig.update_layout(
        
        font=dict(
            family="Inter, sans-serif",
            size=12,
            color="#000000"   
        ),

        plot_bgcolor="rgba(0,0,0,0)",
        paper_bgcolor="rgba(0,0,0,0)",
        margin=dict(t=40, b=40, l=50, r=10),

       
        xaxis=dict(
            showgrid=False,
            title="",
            tickfont=dict(color="#000000", size=11),  
            linecolor="#E2E8F0"
        ),

        
        yaxis=dict(
            gridcolor="#F1F5F9",
            title=dict(
                text="Units Sold",
                font=dict(color="#000000", size=12)  
            ),
            tickfont=dict(color="#000000", size=11),  
            zeroline=False
        ),

       
        legend=dict(
            title=dict(
                text="Segment",
                font=dict(color="#000000", size=12)  
            ),
            orientation="h",
            yanchor="bottom",
            y=1.05,
            xanchor="right",
            x=1,
            font=dict(color="#000000")  
        )
    )

    return fig


def cross_sell_donut(df):
    if df.empty:
        return go.Figure()

    df = df.copy()

   
    total = df["percentage"].sum()
    df["pct"] = (df["percentage"] / total) * 100

    COLORS = ["#4338CA", "#6366F1", "#818CF8", "#A5B4FC"]

    fig = go.Figure(
        data=[
            go.Pie(
                labels=df["item"],
                values=df["pct"],
                hole=0.75,
                marker=dict(
                    colors=COLORS,
                    line=dict(color="#FFFFFF", width=2)
                ),
                hovertemplate="%{label}<br>%{value:.1f}%<extra></extra>",
                textinfo="none"
            )
        ]
    )

    fig.update_layout(
        showlegend=True,
        legend=dict(font=dict(color="#1E293B"), itemclick=False),
        margin=dict(t=10, b=10, l=10, r=10),
        paper_bgcolor="rgba(0,0,0,0)",
        annotations=[
            dict(
                text="Items",
                x=0.5,
                y=0.5,
                font=dict(size=16, family="Inter", color="#0F172A"),
                showarrow=False
            )
        ]
    )

    return fig
