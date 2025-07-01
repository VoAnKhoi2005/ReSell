FROM python:3.11-slim

# Install system dependencies only if needed
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy only requirements file first to use Docker layer cache
COPY requirements.txt .

# Install pip dependencies
# - Install torch CPU-only separately
# - Use --no-cache-dir to avoid local pip cache
RUN pip install --upgrade pip \
 && pip install --no-cache-dir torch==1.13.1+cpu -f https://download.pytorch.org/whl/torch_stable.html \
 && pip install --no-cache-dir -r requirements.txt \
 && apt-get purge -y build-essential \
 && apt-get autoremove -y \
 && rm -rf /root/.cache /var/lib/apt/lists/*

# Copy your actual app code
COPY . .

CMD ["python", "main.py"]
