public class PerlinNoise {
    Noise big;
    Noise middle;
    Noise small;
    Noise smaller;
    public PerlinNoise(int width, int height){
        big = new Noise(64, width, height);
        middle = new Noise(32, width, height);
        small = new Noise(16, width, height);
        smaller = new Noise(8, width, height);
    }
    public float getValue(int x, int y){
        return big.geValue(x, y)*0.45f+ middle.geValue(x, y)*0.25f+small.geValue(x, y)*0.15f+smaller.geValue(x,y)*0.15f;
    }
}
