#ifndef UTILS_HPP
#define UTILS_HPP

#include <vector>
#include <string>

struct ut {
	
	static double mean(const std::vector<double>& v);
	static double stddev(const std::vector<double>& v, double m);
	static std::vector<bool> hip_test(const std::vector<double>& v, float sigma);

	static bool is_number(const std::string& s);
};

#endif
