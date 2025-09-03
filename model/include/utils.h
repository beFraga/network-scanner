#ifndef UTILS_HPP
#define UTILS_HPP

#include <vector>
#include <string>

namespace ut {
	
	double mean(const std::vector<double>& v);
	double stddev(const std::vector<double>& v, double m);
	std::vector<bool> hip_test(const std::vector<double>& v, float sigma);

	bool is_number(const std::string& s);
}

#endif
