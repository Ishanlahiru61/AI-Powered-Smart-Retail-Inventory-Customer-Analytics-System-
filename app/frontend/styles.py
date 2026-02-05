def load_styles():
    return """
    <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

   
    .stApp {
        background-color: #F8FAFC;
        font-family: 'Inter', sans-serif;
    }

    h1, h2, h3, h4, p, span {
        color: #0F172A !important;
    }

    div[data-testid="stMetric"] {
        background: white;
        border: 1px solid #E2E8F0;
        border-radius: 16px;
        padding: 24px !important;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05),
                    0 2px 4px -1px rgba(0,0,0,0.06);
    }

    div[data-testid="stMetricValue"] > div {
        color: #1E293B !important;
        font-weight: 700 !important;
        font-size: 2rem !important;
    }

    
    section[data-testid="stSidebar"] {
        background-color: #FFFFFF !important;
    }

    section[data-testid="stSidebar"] label,
    section[data-testid="stSidebar"] h3 {
        color: #334155 !important;
        font-weight: 500 !important;
    }

    section[data-testid="stSidebar"] hr {
        border-color: #E2E8F0 !important;
    }

   
    .stButton>button {
        background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%) !important;
        color: white !important;
        border: none !important;
        padding: 0.6rem 1.2rem !important;
        border-radius: 10px !important;
        font-weight: 600 !important;
        width: 100%;
        transition: all 0.2s ease !important;
    }

    .stButton>button:hover {
        transform: translateY(-1px) !important;
        box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4) !important;
        background: linear-gradient(135deg, #4F46E5 0%, #4338CA 100%) !important;
    }

    div[data-baseweb="input"],
    div[data-baseweb="select"] {
        border-radius: 8px !important;
    }

  
    .stAlert {
        border-radius: 12px;
    }

    div[data-testid="stDataFrame"] {
        border-radius: 12px;
        border: 1px solid #E2E8F0;
        overflow: hidden;
        background: #FFFFFF;
    }

    div[data-testid="stDataFrame"] thead th {
        background-color: #F1F5F9 !important;
        color: #0F172A !important;
        font-weight: 600 !important;
        font-size: 0.85rem;
        text-transform: uppercase;
    }

    div[data-testid="stDataFrame"] tbody td {
        color: #1E293B !important;
        font-size: 0.85rem;
        border-bottom: 1px solid #E2E8F0;
    }

    div[data-testid="stDataFrame"] tbody tr:hover {
        background-color: #F8FAFC !important;
    }

   
    div[data-testid="stExpander"] {
        background-color: #F8FAFC;
        border-radius: 12px;
        border: 1px solid #E2E8F0;
    }

   
    div[data-testid="stExpander"] > details > summary {
        background-color: #E0F2FE;
        color: #000000;
        padding: 10px 14px;
        border-radius: 12px;
        font-weight: 600;
    }

    
    div[data-testid="stExpander"] > details[open] > div {
        background-color: #F1F5F9;  
        padding: 12px;
        border-radius: 0 0 12px 12px;
        color: #000000;
    }

   
    .stDataFrame {
        color: #000000;
    }

    </style>
    """
