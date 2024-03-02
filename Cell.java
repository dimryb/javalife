import java.util.ArrayList;
import java.util.Random;

public class Cell { //класс клетки
    public int parent_id;
    public String prev_cell;
    public int fraction = 0; //индикатор порванности организма
    int[] mranges = {5, 10, 10, 4, 1000, 3};//пределы генов
    public SimpleGame world;
    public int direction;//направление роста
    public int id;
    public int lifetime = 200;
    public int x;//координаты
    public int y;
    public float energy;//энергия
    public int type = 3;//тип клетки 0-корень 1-антенна 2 - лист, 3 - семачка
    public int[][] genom; //генокод
    public ArrayList<String> relations = new ArrayList<>();
    public int active_gen;
    final private Random rand = new Random();//ГПСЧ
    public int[] color = {rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)};
    public Cell(int x, int y, float energy, int active_gen){
        this.x = x;
        this.y = y;
        this.energy = energy;
        int[][] randgenom = new int[10][4];//генерируем случайный геном
        for(int j=0; j < 10;j++){
            int[] newgen = {rand.nextInt(mranges[0]), rand.nextInt(mranges[1]), rand.nextInt(mranges[2]), rand.nextInt(mranges[3]), rand.nextInt(mranges[4]), rand.nextInt(mranges[5])};
            randgenom[j] = newgen;
        }
        this.genom = randgenom;

        this.active_gen=active_gen;
    }
    public boolean CanGrow(){//проверка возможности делится
        if(type == 3){
            if(lifetime<=0){
                return true;
            }
            return false;
        }
        switch (genom[active_gen][5]){
            case 0:
                return true;
            case 1:
                if(energy >= 10){
                    return true;
                }
                break;
            case 2:
                if(lifetime <= 0){
                    return true;
                }
                break;
        }
        return false;
    }
    public int[] Move(){//движение
        int width =world.width;
        int height =world.height;
        if(rand.nextInt(2) == 0) {
            int nx = x + rand.nextInt(-1, 2);
            if (nx == -1) {
                nx = width - 1;
            }
            if (nx == width) {
                nx = 0;
            }
            int ny = y + rand.nextInt(-1, 2);
            if (ny == -1) {
                ny = height - 1;
            }
            if (ny == height) {
                ny = 0;
            }
            int[] output = {nx, ny};
            return output;
        } else {
            int[][] pos = {{0, 1},{1, 0},{0, -1}, {-1, 0}};
            int nx = x+pos[direction][0];
            if(nx == -1){nx=width-1;}
            if(nx == width){nx=0;}
            int ny = y+pos[direction][1];
            if(ny == -1){ny=height-1;}
            if(ny == height){ny=0;}
            int[] output = {nx, ny};
            return output;
        }
    }
    public Cell Mitoz(){//Деление
        if(genom[active_gen][0] ==3 && type==3){
            genom[active_gen][0] = 2;
        }
        int width =world.width;
        int height =world.height;
        int maxid = world.maxid;
        int[][] pos = {{0, 1},{0, -1}, {1, 0}, {-1, 0}};
        int nx = x+pos[genom[active_gen][3]][0];
        if(nx == -1){nx=width-1;}
        if(nx == width){nx=0;}
        int ny = y+pos[genom[active_gen][3]][1];
        if(ny == -1){ny=height-1;}
        if(ny == height){ny=0;}
        energy /= 2;
        Cell kid = new Cell(nx, ny, energy, genom[active_gen][1]);//0 - тип потомка, 1 - активный ген потомка, 2 - след. активный ген, 3 - направление роста потомка, 4 - lifetime потомка 5 - условия роста
        kid.lifetime = genom[active_gen][4];
        for(int i = 0; i<10; i++){
            kid.genom[i]=genom[i].clone();
        }
        kid.direction = genom[active_gen][3];
        kid.world = world;
        kid.type=genom[active_gen][0];
        kid.color = color.clone();
        kid.id = maxid;
        if(kid.type!=3){
            kid.prev_cell = Integer.toString(id);
            relations.add(Integer.toString(maxid));
            kid.parent_id = parent_id;
            kid.active_gen = 0;
            if(rand.nextInt(10) == 0){//мутация
                int mpos = rand.nextInt(6);
                kid.genom[rand.nextInt(10)][mpos] =rand.nextInt(mranges[mpos]);
                kid.color[rand.nextInt(3)] += rand.nextInt(-1, 1)*5;
            }
        } else {
            kid.active_gen = 0;
            kid.parent_id = kid.id;
            if(rand.nextInt(2) == 0){//мутация
                int mpos = rand.nextInt(6);
                kid.genom[rand.nextInt(10)][mpos] =rand.nextInt(mranges[mpos]);
                kid.color[rand.nextInt(3)] += rand.nextInt(-1, 2)*5;
            }
        }
        active_gen=genom[active_gen][2];
        return kid;
    }
    public int[] Eat(int[] pos){//кушац
        switch (type){
            case 0:
                if(pos[0] > 0){
                    pos[0] -= 1;
                    if(world.worldmap[x][y]==0){
                        energy+=2.25;
                    }
                    if(world.worldmap[x][y]==1){
                        energy+=2;
                    }
                    if(world.worldmap[x][y]==2){
                        energy+=1.75;
                    }
                    energy += 2;
                }
                return pos;
            case 1:
                if(pos[1] > 0){
                    pos[1] -= 1;
                    if(world.worldmap[x][y]==0){
                        energy+=2.25;
                    }
                    if(world.worldmap[x][y]==1){
                        energy+=2;
                    }
                    if(world.worldmap[x][y]==2){
                        energy+=1.75;
                    }
                }
                return pos;
            case 2:
                if(world.worldmap[x][y]==0){
                    energy+=world.daynight-0.1;
                }
                if(world.worldmap[x][y]==1){
                    energy+=world.daynight;
                }
                if(world.worldmap[x][y]==2){
                    energy+=world.daynight+0.1;
                }
                return pos;
        }
        return pos;
    }
}
