package nl.brendanspijkerman.discustrajectorycalculator.models;

import java.io.InputStream;

/**
 * Created by brendan on 20/01/2017.
 * Class that holds the arguments to be passed to the Async DataDiscusStreamReader class
 */

public class DataDiscusStreamReaderParameters {

    public InputStream inputStream;
    public DataDiscus dataDiscus;

    DataDiscusStreamReaderParameters(InputStream _inputStream, DataDiscus _dataDiscus) {

        inputStream = _inputStream;
        dataDiscus = _dataDiscus;

    }
}
