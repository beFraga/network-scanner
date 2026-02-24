from flask import Flask, request, jsonify
import os
import sys 

from plotter import Plotter 

app = Flask(__name__)

# secret key from ambient environments
INTERNAL_SECRET_KEY = os.getenv('INTERNAL_SECRET_KEY')

@app.route('/generate-plot', methods=['POST'])
def generate_plot():
    # 2.security call
    client_secret = request.headers.get('X-Internal-Secret')

    if not client_secret or client_secret != INTERNAL_SECRET_KEY:
        return jsonify({"error" : "Access denied. Origin non authorized"}), 403
    
    try:
        # catches all data from java
        data = request.json
        start_date = data.get('start_date')
        end_date = data.get('end_date')
        headers = data.get('headers', [])

        # calls the plotting logic
        plotter = Plotter()

        # plotter generate files on a shared directory (/app/out_plot)
        plotter(headers, start_date, end_date)

        return jsonify({"message" : "Graphics generated with success"}), 200
    
    except Exception as e:
        return jsonify({"error" : str(e)}), 500
    

if __name__ == '__main__':
    # runs server in port 6767
    app.run(host='0.0.0.0', port=6767)