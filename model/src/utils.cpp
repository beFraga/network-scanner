#include "utils.h"
#include <vector>
#include <cmath>

namespace ut {

	double mean(const std::vector<double>& v) {
		double sum = 0.0;
		for(double x : v) sum += x;
		return sum / v.size();
	}

	double stddev(const std::vector<double>& v, double m) {
		double sum = 0.0;
		for (double x : v) sum += (x - m) * (x - m);
		return sqrt(sum / v.size());
	}

	std::vector<bool> hip_test(const std::vector<double>& v, float sigma) {
		double m = mean(v);
		double s = stddev(v, m);
		std::vector<bool> outliers;
		for (double x : v) {
			outliers.push_back(x > m + sigma * s);
		}
		return outliers;
	}

	bool is_number(const std::string& s) {
		try {
			std::stod(s);
			return true;
		}
		catch (...){
			return false;
		}
	}
}
