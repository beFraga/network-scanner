import sys
import json
import matplotlib as plt
from collections import Counter

class Plotter():
    def __init__(self):
        self.file = "data.json"
        self.switch = {
            "payloadSize": self.plot_histogram,
            "remoteAddress": self.plot_pie,
            "sequenceNumber": self.plot_pie,
            "localAddress": self.plot_pie,
            "protocol": self.plot_pie,
            "remotePort": self.plot_pie,
            "method": self.plot_pie
        }


    def read_data(self):
        with open(self.file) as f:
            d = json.load(f)
        self.data = d

    def plot_histogram(self, d, h):
        plt.hist(d, bins=10)

        plt.xlabel('Value')
        plt.ylabel('Frequency')
        plt.title(f"Anomaly Histogram: {h}")

        plt.show()

    def plot_pie(self, d, h):
        counts = Counter(d)
        labels = list(counts.keys())
        sizes = list(counts.values())

        plt.pie(sizes, labels=labels, autopct='%1.1f%%')
        plt.title(f"Value Distribuition in {h}")

        plt.show()

    def plot_pie(self):
        values = [item["flag"] for item in self.data]
        counts = Counter(values)
        label_map = {
            True: "Anômalia",
            False: "Normal"
        }
        labels = [label_map[k] for k in counts.keys()]
        sizes = list(counts.values())

        plt.pie(sizes, labels=labels, autopct='%1.1f%%')
        plt.title(f"Value Distribuition of Anomalies")

        plt.show()



    def __call__(self, headers):
        self.read_data()

        for h in headers:
            d = [item[h] for item in self.data if item["flag"]]
            self.switch["h"](d, h)

        self.plot_pie()


if __name__ == "__main__":
    plotter = Plotter()
    plotter(sys.argv)
