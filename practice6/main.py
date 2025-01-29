#!/usr/bin/env python3

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, min, max, avg, to_date, month
from pyspark.sql.types import StructType, StructField, StringType, DoubleType
import datetime

# Initialize Spark session
spark = (
    SparkSession.builder.appName("WeatherDataAnalysis")
    .master("spark://localhost:7077")
    .getOrCreate()
)

# Define the schema for the DataFrame
schema = StructType(
    [
        StructField("timestamp", StringType(), True),
        StructField("temperature", DoubleType(), True),
    ]
)

data_list = []
with open("dataexport.csv", "r") as file:
    for line in file:
        parts = line.strip().split(",")
        try:
            datetime.datetime.strptime(parts[0], "%Y%m%dT%H%M")
            data_list.append((parts[0], float(parts[1])))
        except ValueError:
            continue

# Create a DataFrame from the list
data = spark.createDataFrame(data_list, schema=schema)

# Convert the timestamp to a date and extract the month
data = data.withColumn("date", to_date(col("timestamp"), "yyyyMMdd'T'HHmm"))
data = data.withColumn("month", month(col("date")))

# Group by month and calculate min, max, and average temperatures
result = data.groupBy("month").agg(
    min("temperature").alias("min_temperature"),
    max("temperature").alias("max_temperature"),
    avg("temperature").alias("avg_temperature"),
)

# Show the result
result.show()

# Stop the Spark session
spark.stop()
