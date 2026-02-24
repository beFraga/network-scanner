import sys
import json
import matplotlib
matplotlib.use('Agg') 
import matplotlib.pyplot as plt
from collections import Counter
import pandas as pd
import mysql.connector
import os

DB_CONFIG = {
    'user': os.getenv('DATABASE_USERNAME_NETWORK_SCANNER'),
    'password': os.getenv('DATABASE_PASSWORD_NETWORK_SCANNER'),
    'host': 'db', # Nome do serviço no Docker Compose ou 'localhost'
    'database': os.getenv('DATABASE_NAME_NETWORK_SCANNER'),
    'port': 3306
}

class Plotter():
    def __init__(self):
        self.switch = {
            "payloadSize": self.plot_histogram,
            "remoteAddress": self.plot_pie,
            "sequenceNumber": self.plot_pie,
            "localAddress": self.plot_pie,
            "protocol": self.plot_pie,
            "remotePort": self.plot_pie,
            "method": self.plot_pie
        }
        self.data = None
    
    # reads the data from the db filtering data intervals 
    def read_data(self, start_date, end_date):
        try:
            # connects to database
            conn = mysql.connector.connect(**DB_CONFIG)

            # sql query
            query = """
                SELECT * FROM tcp_infos
                WHERE received_at BETWEEN %s AND %s
            """
            
            # pandas reads directly into a DataFrame
            self.data = pd.read_sql(query, conn, params=(start_date, end_date))
            conn.close()

            if self.data.empty:
                print("No data found for that selected period")
                sys.exit(0)
            
        except mysql.connector.Error as err:
            print(f"Couldn't connect to database: {err}")
            sys.exit(1)

    # generates the histogram
    def plot_histogram(self, series, column_name):
        plt.figure()
        plt.hist(series, bins=10, color='skyblue')
        plt.xlabel('Value')
        plt.ylabel('Frequency')
        plt.title(f"Anomaly Histogram: {column_name}")

        plt.savefig(f"/app/out_plot/{column_name}.png")
        plt.close()

    # generates the pie plot
    def plot_pie(self, series, column_name):
        counts = series.value_counts()
        plt.figure()
        plt.pie(counts, labels=counts.index, autopct='%1.1f%%')
        plt.title(f"Value Distribution in {column_name}")

        plt.savefig(f"/app/out_plot/{column_name}.png")
        plt.close()

    def plot_pie_complete(self, start_date, end_date):
        try:
            conn = mysql.connector.connect(**DB_CONFIG,
            auth_plugin='mysql_native_password')
            query = """
            SELECT flag, COUNT(*) as count
            FROM tcp_infos
            WHERE received_at BETWEEN %s AND %s
            GROUP BY flag
        """
            df = pd.read_sql(query, conn, params=(start_date, end_date))
            conn.close()

            if not df.empty:
                labels = df['flag'].map({1: 'Anomaly', 0: 'Regular'})

                plt.figure()
                plt.pie(df['count'], labels=labels, autopct='%1.1f%%', colors=['green', 'red'])
                plt.title(f"Value Distribution of Anomalies")
                plt.savefig(f"/app/out_plot/general-{start_date}.png")
                plt.close()

        except Exception as e:
            print(f"Error generating complete graphic: {e}")

    def __call__(self, headers, start_date, end_date):
        print(f"Generating graphics for {headers} from {start_date} to {end_date}")
        self.read_data(start_date, end_date)

        for h in headers:
            # verifies if there is an existing column in the mapping and returned date
            if h in self.switch and h in self.data.columns:
                # calls the function to pass the whole column and name
                self.switch[h](self.data[h], h)
            else:
                print(f"Warning: Column {h} not found in database or not mapped.")

        # general plot
        self.plot_pie_complete(start_date, end_date)
