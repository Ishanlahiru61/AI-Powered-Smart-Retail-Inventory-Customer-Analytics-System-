from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .api_routes import router  

app = FastAPI(title="Grocery Analytics API")

# Allow Streamlit or other frontends to access this API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include the router
app.include_router(router, prefix="/api")

@app.get("/")
def root():
    return {"message": "Grocery Analytics API Running!"}
