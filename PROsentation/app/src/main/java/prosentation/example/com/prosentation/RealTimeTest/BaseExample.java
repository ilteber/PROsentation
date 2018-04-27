package prosentation.example.com.prosentation.RealTimeTest;

import com.jjoe64.graphview.GraphView;

/**
 * Created by ERKAN-PC on 19.04.2018.
 */

public abstract class BaseExample {
    //public abstract void onCreate(FullscreenActivity fullscreenActivity);
    public abstract void initGraph(GraphView graph);

    public void onPause() {}
    public void onResume() {}
}
