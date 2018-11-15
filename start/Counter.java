/*
 * Mylab 
 * All right reserved  * 
 */
package start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author de
 */
public class Counter implements ActionListener, InitConstants {

    private long count = 0;
    private Timer time;

    public Counter() {
        time = new Timer(timer1sec, this);
        time.start();
    }

    public void resetCounter () {
        count = 0L;
    }
    
    public void stopCounter() {
        if (time.isRunning()) {
            time.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
    }

    public long getCounter() {
        return count;
    }
}
