package wekaTest;

import java.util.Arrays;

public class Statistics {
	Double[] data;
    int size;

    public Statistics(Double[] data) {
        this.data = data;
        size = data.length;
    }

    Double getMean() {
        Double sum = 0.0;
        for(Double a : data)
            sum += a;
        return sum/size;
    }

    Double getVariance() {
        Double mean = getMean();
        Double temp = 0.0;
        for(Double a :data)
            temp += (a-mean)*(a-mean);
        return temp/(size-1);
    }

    Double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public Double median() {
       Arrays.sort(data);

       if (data.length % 2 == 0) {
          return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
       }
       return data[data.length / 2];
    }
}
