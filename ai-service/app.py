import os
from fastapi import FastAPI, HTTPException

from pydantic import BaseModel
from google import genai  # Correct new SDK import
from tenacity import retry, wait_exponential, stop_after_attempt

from dotenv import load_dotenv
import time

load_dotenv()

app = FastAPI()

# 1. Initialize the NEW Client
# The SDK automatically looks for GEMINI_API_KEY in your .env
client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

class AnalysisRequest(BaseModel):
    file_name: str
    code: str
# 2. Updated Retry Logic
@retry(
    wait=wait_exponential(multiplier=1, min=4, max=60),
    stop=stop_after_attempt(5),
    reraise=True
)
def call_gemini(prompt):
    # Use the unified client.models.generate_content syntax
    response = client.models.generate_content(
        model="gemini-2.5-flash", 
        contents=prompt
    )
    time.sleep(4) 
    return response.text

@app.post("/analyze")
async def analyze_code(request: AnalysisRequest):
    # Safety check for empty content
    if not request.code.strip():
        return {"analysis": "Empty file - no logic to analyze."}

    prompt = f"""
    You are a Senior Full-Stack Developer. 
    Explain the purpose and logic of this file: {request.file_name}
    
    Code Content:
    {request.code}
    
    Rules:
    - Keep it under 3 concise sentences.
    - Mention key technologies or patterns used.
    - If it's a configuration file, explain what it configures.
    """

    try:
        # 3. Call the retry-wrapped function
        analysis_text = call_gemini(prompt)
        return {"analysis": analysis_text}
    except Exception as e:
        print(f"❌ GEMINI CRASHED: {str(e)}")
        raise HTTPException(status_code=500, detail=f"AI Analysis failed: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)