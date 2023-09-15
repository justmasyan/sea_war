package ball.com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

class shot{
    int x = 0;
    int y = 0;
    int radius = 0;
    boolean status;
    shot(int x,int y){
        this.x = x;
        this.y = y;
        radius = 10;
        status = false;
    }

}
class Thread_of_elem extends Thread{
    shot bam_bam;
    final int WEIGHT;
    final int HEIGHT;
    final int start_position;
    Thread_of_elem(shot bam_bam,int x,int y){
        this.bam_bam = bam_bam;
        WEIGHT = x;
        HEIGHT = y;
        start_position = bam_bam.x;
    }
    Thread_of_elem(){
        WEIGHT = 10;
        HEIGHT = 10;
        start_position = 10;
    }



    @Override
    public void run(){

        while(true) {
            bam_bam.x++;

            try {
                sleep(0,500);
            } catch (InterruptedException exc) {

            }
            if(bam_bam.x + bam_bam.radius == WEIGHT)
                break;
        }

        bam_bam.status = false;
        bam_bam.x = start_position;
    }

}

class ship extends Thread{
    int x_ship = 600;
    int y_ship = 10;
    int radius = 10;

    @Override
    public void run(){
        while(true){
            y_ship++;
            if(y_ship + 10 == 480)
            y_ship = 0;
            try{
                sleep(0,500);
            } catch (InterruptedException exc){

            }
        }
    }
}

class Graphic extends Canvas implements Runnable{
    public static boolean WORK_GRAPHIC = false;
    Thread[] streams = new Thread[4];
    public static shot[] arr_shot = new shot[4];
    public static ship boat = new ship();

    public class keyListener implements KeyListener{

        @Override
        public void keyPressed(KeyEvent e){
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                for(int i = 0;i < 4;i++) {
                    if(arr_shot[i].status == false) {
                        streams[i] = new Thread_of_elem(arr_shot[i], getWidth(), getHeight());
                        streams[i].start();
                        arr_shot[i].status = true;
                        break;
                    }
                }
            }

            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                WORK_GRAPHIC = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e){

        }

        @Override
        public void keyTyped(KeyEvent e){

        }
    }
    public void init(){
        addKeyListener(new keyListener());
        for(int i = 0;i < 4;i++)
            arr_shot[i] = new shot(50,160);

        Thread stream_boat = boat;
        stream_boat.start();
    }

    public void start(){
        WORK_GRAPHIC = true;
        new Thread(this).run();
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        BufferStrategy bs_shot = getBufferStrategy();
        BufferStrategy bs_ship = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(2);
            requestFocus();
            return ;
        }
        Graphics gs = bs.getDrawGraphics();
        gs.setColor(Color.BLACK);
        gs.fillRect(0,0,getWidth(),getHeight());
        gs.setColor(Color.GREEN);
        gs.fillRect(0,getHeight()/2-30,20,60);
        gs.fillRect(20,getHeight()/2-10,20,20);
        gs.dispose();
        bs.show();

        for(int k = 0;k < 4;k++) {
            if (arr_shot[k].status == true) {
                Graphics gs_shot = bs_shot.getDrawGraphics();
                gs_shot.setColor(Color.PINK);

                for (int i = arr_shot[k].x - arr_shot[k].radius; i <= arr_shot[k].x + arr_shot[k].radius; i++) {
                    for (int j = arr_shot[k].y - arr_shot[k].radius; j <= arr_shot[k].y + arr_shot[k].radius; j++) {
                        if (Math.pow((double) i - arr_shot[k].x, 2) + Math.pow((double) j - arr_shot[k].y, 2) <= Math.pow((double) arr_shot[k].radius, 2))
                            gs_shot.fillRect(i, j, 1, 1);
                    }
                }

                gs_shot.dispose();
                bs_shot.show();
            }
        }

        Graphics gs_ship = bs_ship.getDrawGraphics();
        gs_ship.setColor(Color.YELLOW);
        gs_ship.fillRect(boat.x_ship, boat.y_ship, boat.radius,boat.radius);
        gs_ship.dispose();
        bs_ship.show();

    }

    public void run(){
        init();

        while(WORK_GRAPHIC){

            render();

            update();

            try{
                Thread.sleep(10);
            } catch(InterruptedException exc){

            }
        }
    }

    public void update(){

    }

    public boolean getWork(){
        return WORK_GRAPHIC;
    }
}


public class Main{
    final public static int WIDTH = 640;
    final public static int HEIGHT = 320;

    public static void main(String[] args) {
        Graphic ball = new Graphic();
//        ball.setPreferredSize(new Dimension(WIDTH,HEIGHT));

        JFrame fps = new JFrame("ball");
        ball.setPreferredSize(new Dimension(WIDTH,HEIGHT));

        Graphic X = new Graphic();
        Graphic.keyListener Y = X.new keyListener();
        fps.addKeyListener(Y);

        fps.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fps.setLayout(new BorderLayout());
        fps.add(ball,BorderLayout.CENTER);
        fps.pack();
        fps.setResizable(false);
        fps.setVisible(true);
        ball.start();

        if(ball.getWork() == false) {
            fps.dispatchEvent(new WindowEvent(fps, WindowEvent.WINDOW_CLOSING));
            fps.dispose();
        }
    }
}


//Нужно:
//1) Работа с потоком
//2) Графический интерфейс