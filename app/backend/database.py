import certifi
import os
from pymongo import MongoClient
from dotenv import load_dotenv

load_dotenv()


MONGO_URI = os.getenv("MONGO_URI")
if not MONGO_URI:
    raise ValueError("MONGO_URI not found in .env file!")

ca = certifi.where()


DB_NAME = "grocery_store"

client = MongoClient(MONGO_URI, tlsCAFile=ca)
db = client[DB_NAME]


def get_collection(collection_name):
    return db[collection_name]
