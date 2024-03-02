import java.util.Random;
public class Noise {
    int scale;
    int[][] randtable;
    int width;
    int height;
    Random generator = new Random();
    private int rand(int x, int y){
        return randtable[x][y];
    }
    public Noise(int scale, int width, int height){
        this.scale = scale;
        this.randtable= new int[width/scale+2][height/scale+2];
        this.width=width/scale+1;
        this.height=height/scale+1;
        for(int i = 0; i< this.width; i++){
            for(int j = 0; j< this.height; j++){
                randtable[i][j] = generator.nextInt(2);
            }
        }
        for(int i = 0; i < this.width; i++){
            randtable[i][this.height-1]=randtable[i][0];
        }
        for(int i = 0; i < this.height; i++){
            randtable[this.width-1][i]=randtable[0][i];
        }

    }
    public float geValue(int x, int y){
        int xgs = x / scale;
        int ygs = y / scale;
        int xge = x / scale +1;
        int yge = y / scale +1;
        int xs = xgs*scale;
        int xe = xge * scale;
        int ys = ygs*scale;
        int ye = yge * scale;
        float k = (xe-xs)*(ye-ys);
        float w11 = (xe-x)*(ye-y)/k;
        float w12 = (xe-x)*(y-ys)/k;
        float w21 = (x-xs)*(ye-y)/k;
        float w22 = (x-xs)*(y-ys)/k;
        return rand(xgs, ygs)*w11+rand(xgs, yge)*w12+rand(xge, ygs)*w21+rand(xge, yge)*w22;
    }
}
