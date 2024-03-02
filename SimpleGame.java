import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class SimpleGame extends JPanel implements ActionListener, KeyListener {
    private int lulz = 0;
    private int display = 0;
    public float daynight = 2;//смена дня и ночи
    private boolean time = false;
    float dsize = 0.6f;//движение и увеличение дисплея
    int dx;
    public int mouseclickx;
    public int mouseclicky;
    int dy;
    public int[][] selectgenom = new int[10][6];
    public int maxid = 101;
    public int width = 320;
    public int height = 320;
    private PerlinNoise noise = new PerlinNoise(width, height);
    private Timer timer;  // Таймер для обновления экрана
    public HashMap<String, Cell> cells = new HashMap<>();//список клеток
    public float[][] worldmap = new float[width][height];
    public int[][] cellmap = new int[width][height];//карты
    public int[][][] foodmap = new int[width][height][2];
    private Random rand = new Random();
    public SimpleGame() {
        addMouseListener(new MouseListen(this));
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(50, this);  // Тут создаем таймер
        timer.start();  // В этой строчке его запускаем
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("life simulation");
        SimpleGame game = new SimpleGame();
        frame.add(game);
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int i = 0; i < width; i ++){//рисуем еду
            for(int j = 0; j < height; j ++){
                int green = 0;
                switch (Math.round(worldmap[i][j])){
                    case -1:
                        green = 0;
                        break;
                    case 0:
                        green = 63;
                        break;
                    case 1:
                        green = 127;
                        break;
                    case 2:
                        green = 191;
                        break;
                    case 3:
                        green = 255;
                        break;

                }
                g.setColor(new Color(foodmap[i][j][0],green,foodmap[i][j][1]));
                g.fillRect(Math.round((i*5+dx)*dsize),Math.round((j*5+dy)*dsize),Math.round(5*dsize),Math.round(5*dsize));
            }
        }
        for(Cell cell: cells.values()){
            switch (display) {
                case 0:
                    switch (cell.type) {//собственно рисуем
                        case 0:
                            g.setColor(new Color(128, 100, 0));
                            g.fillRect(Math.round((cell.x * 5 + 1+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(3*dsize), Math.round(3*dsize));
                            break;
                        case 1:
                            g.setColor(new Color(128, 255, 251));
                            if(cell.direction < 2) {
                                g.drawRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.drawRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 2:
                            g.setColor(Color.GREEN);
                            if(cell.direction < 2) {
                                g.fillRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.fillRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 3:
                            g.setColor(Color.yellow);
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            break;
                        case 4:
                            g.setColor(Color.red);
                            g.fillOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                    }
                    break;
                case 1:
                    g.setColor(new Color(Math.abs(cell.parent_id)%256, Math.abs(cell.parent_id*4)%256, Math.abs(cell.parent_id*16)%256));
                    switch (cell.type) {//собственно рисуем
                        case 0:
                            g.fillRect(Math.round((cell.x * 5 + 1+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(3*dsize), Math.round(3*dsize));
                            break;
                        case 1:
                            if(cell.direction < 2) {
                                g.drawRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.drawRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 2:
                            if(cell.direction < 2) {
                                g.fillRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.fillRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 3:
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            break;
                        case 4:
                            g.fillOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                    }
                    break;
                case 2:
                    g.setColor(new Color(Math.min(255, Math.max(0, cell.color[0])), Math.min(255, Math.max(0, cell.color[1])), Math.min(255, Math.max(0, cell.color[2]))));
                    switch (cell.type) {//собственно рисуем
                        case 0:
                            g.fillRect(Math.round((cell.x * 5 + 1+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(3*dsize), Math.round(3*dsize));
                            break;
                        case 1:
                            if(cell.direction < 2) {
                                g.drawRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.drawRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 2:
                            if(cell.direction < 2) {
                                g.fillRect(Math.round((cell.x * 5 + 1 + dx) * dsize), Math.round((cell.y * 5 + dy) * dsize), Math.round(3 * dsize), Math.round(5 * dsize));
                            } else {
                                g.fillRect(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+1+dy)*dsize), Math.round(5*dsize), Math.round(3*dsize));
                            }
                            break;
                        case 3:
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            break;
                        case 4:
                            g.fillOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                            g.drawOval(Math.round((cell.x * 5+dx)*dsize), Math.round((cell.y * 5+dy)*dsize), Math.round(5*dsize), Math.round(5*dsize));
                    }
                    break;
            }
        }
        g.setColor(Color.lightGray);
        for (Cell cell1: cells.values()){//перераспределение энергии
            for(String kid: cell1.relations){
                if(cells.keySet().contains(kid)) {
                    Cell cell2 = cells.get(kid);
                    if (Math.abs(cell1.x + cell1.y - cell2.x - cell2.y) <= 2) {
                        g.drawLine(Math.round((cell1.x * 5 + 2 + dx) * dsize), Math.round((cell1.y * 5 + 2 + dy) * dsize), Math.round((cell2.x * 5 + 2 + dx) * dsize), Math.round((cell2.y * 5 + 2 + dy) * dsize));
                    }
                }
            }
        }
        g.setColor(Color.black);//время суток
        g.fillRect(1200, 0, 1000, 1200);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.white);
        g.drawString("таймер: " + lulz + " освещенность: " + daynight, 1210, 30);
        if(selectgenom.length != 0){

            int y = 0;
            for(int[] i: selectgenom){
                y+=1;
                int x = 0;
                for(int j : i){
                    x+=1;
                    if(x == 6){
                        x+=2;
                    }
                    if (x == 1){
                        switch (j) {//собственно рисуем
                            case 0:
                                g.setColor(new Color(128, 0, 128));
                                g.fillRect(1215, 15+y*30, 4, 4);
                                break;
                            case 1:
                                g.setColor(new Color(128, 255, 251));
                                g.drawRect(1215, 15+y*30, 4, 6);
                                break;
                            case 2:
                                g.setColor(Color.GREEN);
                                g.fillRect(1215, 15+y*30, 4, 6);
                                break;
                            case 3:
                                g.setColor(Color.yellow);
                                g.drawOval(1215, 15+y*30, 6, 6);
                                break;
                            case 4:
                                g.setColor(Color.red);
                                g.fillOval(1215, 15+y*30, 6, 6);
                                g.drawOval(1215, 15+y*30, 6, 6);
                        }
                        g.setColor(Color.white);
                    }
                    else {
                        g.drawString(Integer.toString(j), 1210 + x * 20, 30 + y * 30);
                    }
                }
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(lulz == 0){

            for(int i = 0; i < width; i ++){//генерируем карту еды
                for(int j = 0; j < height; j ++){
                    foodmap[i][j][0]=rand.nextInt(10);
                    foodmap[i][j][1]=rand.nextInt(10);
                }
            }
            for(int i = 0; i < width; i ++){//генерируем карту высот
                for(int j = 0; j < height; j ++){
                    worldmap[i][j]=noise.getValue(i, j);
                    if(worldmap[i][j] < 0.2){
                        worldmap[i][j]=-1;
                    }
                    else if(worldmap[i][j] < 0.4){
                        worldmap[i][j]=0;
                    }
                    else if(worldmap[i][j] < 0.6){
                        worldmap[i][j]=1;
                    } else if(worldmap[i][j] < 0.8){
                        worldmap[i][j]=2;
                    }
                    else {
                        worldmap[i][j]=3;
                    }
                }
            }
            for(int i = 0; i < 100; i++) {//генерируем семена
                Cell ncell = new Cell(rand.nextInt(width), rand.nextInt(height), rand.nextFloat(40,50), 0);
                if(worldmap[ncell.x][ncell.y] != -1 && worldmap[ncell.x][ncell.y] != 3) {
                    ncell.parent_id = i;
                    ncell.world = this;
                    ncell.id = i+1;
                    ncell.lifetime = 1;
                    cells.put(Integer.toString(i), ncell);
                }
            }
        }
        lulz +=1;
        if(time){//цикл дня и ночи
            daynight -= 0.001;
            if(daynight <= 1){
                daynight -= 0.006;
            }
            if(daynight <= 0.90){
                time = false;
            }
        } else  {
            daynight += 0.001;
            if(daynight <= 1){
                daynight += 0.006;
            }
            if(daynight >=1.5){
                time = true;
            }
        }
        for(int i = 0; i < width; i ++){
            for(int j = 0; j < height; j ++){
                cellmap[i][j]=0;
            }
        }
        for(Cell cell: cells.values()){//заполняем карту клеток
            cellmap[cell.x][cell.y]=cell.id;
        }
        HashMap<String, Cell> cells1 = (HashMap<String, Cell>) cells.clone();

        for (Cell cell: cells.values()){//рисуем и обрабатываем клетки
            cell.lifetime-=1;
            if(cell.type != 3) { //не для семечек
                cell.energy -= 1;
                if ((foodmap[cell.x][cell.y][0] >= 200 || foodmap[cell.x][cell.y][1] >= 200) && cell.type != 0 && cell.type != 1)//клетку дамажит плохая земля
                {
                    cell.lifetime -= foodmap[cell.x][cell.y][0]/10;
                    cell.energy -= 5;
                }
                if(cell.type == 4){//хищник
                    int[][] neigbors = {{cell.x+1, cell.y},{cell.x-1, cell.y},{cell.x, cell.y+1},{cell.x, cell.y-1},
                            {cell.x+1, cell.y+1},{cell.x-1, cell.y-1},{cell.x-1, cell.y+1},{cell.x+1, cell.y-1}};
                    for(int[] pos: neigbors){
                        if(pos[0] == width){
                            pos[0]=0;
                        } if(pos[0] == -1){
                            pos[0]=width-1;
                        }
                        if(pos[1] == height){
                            pos[1]=0;
                        }
                        if(pos[1] == -1){
                            pos[1]=height-1;
                        }
                        if(cells1.keySet().contains(Integer.toString(cellmap[pos[0]][pos[1]]))){
                            Cell food = cells1.get(Integer.toString(cellmap[pos[0]][pos[1]]));
                            if(food.parent_id != cell.parent_id && !(food.relations.contains(Integer.toString(cell.id))||cell.relations.contains(Integer.toString(food.id))) && food.type !=0){//корни и родственников есть нельзя
                                cell.energy += food.energy;

                                cells1.remove(Integer.toString(cellmap[pos[0]][pos[1]]));
                            }

                        }
                    }
                } else {
                    foodmap[cell.x][cell.y] = cell.Eat(foodmap[cell.x][cell.y]);//клетка кушает всегда
                }
            } else {//семечки летают
                int[] nextpos = cell.Move();
                if(cellmap[nextpos[0]][nextpos[1]] == 0){
                    cellmap[cell.x][cell.y] = 0;
                    cellmap[nextpos[0]][nextpos[1]] = cell.id;
                    if(worldmap[cell.x][cell.y] !=worldmap[nextpos[0]][nextpos[1]]){
                        cell.lifetime/=2;
                    }
                    cell.x = nextpos[0];
                    cell.y = nextpos[1];
                } else {
                    cell.lifetime -= 3;//семечка дамажит всех, с кем сталкивается
                    Cell jertva = cells1.get(Integer.toString(cellmap[nextpos[0]][nextpos[1]]));
                    if(jertva != null) {
                        jertva.lifetime -= 10;
                        for(String kid: jertva.relations){
                            if(cells1.keySet().contains(kid)){
                                cells1.get(kid).fraction +=1;
                            }
                        }
                    }
                }
            }
            if(cell.CanGrow() && cell.energy >= 3){//клетка делится
                Cell ncell = cell.Mitoz();
                if(cellmap[ncell.x][ncell.y]==0 && (worldmap[cell.x][cell.y]==worldmap[ncell.x][ncell.y]||rand.nextInt(10)== 0) && worldmap[ncell.x][ncell.y] != -1 && worldmap[ncell.x][ncell.y] != 3) {//переход границы биомов карается
                    cells1.put(Integer.toString(maxid), ncell);
                    cellmap[ncell.x][ncell.y]=ncell.id;
                }
                if(cell.type == 3){
                    cell.lifetime = -100;
                    cell.energy = -100;
                }
//                else if (cellmap[ncell.x][ncell.y]!=0 && cell.relations.size()<= 1){//маленький шанс на сращицание TODO: попробуй эту фигню заменить проверкой на наличие relation между клетками
//                    if(cells1.keySet().contains(Integer.toString(cellmap[ncell.x][ncell.y]))){
//                        if(cell.parent_id == cells1.get(Integer.toString(cellmap[ncell.x][ncell.y])).parent_id  && cell.fraction != cells1.get(Integer.toString(cellmap[ncell.x][ncell.y])).fraction){
//                            cell.relations.add(Integer.toString(cellmap[ncell.x][ncell.y]));
//                        }
//                    }
//                }
                maxid++;
            }
            if(cell.energy <= 0 || cell.lifetime <= 0){//клетка сдохла
                if(cell.energy > 0) {
                    foodmap[cell.x][cell.y][0] += cell.energy/2;//трупик
                    foodmap[cell.x][cell.y][1] += cell.energy/2;
                    foodmap[cell.x][cell.y][0] = Math.min(foodmap[cell.x][cell.y][0], 255);
                    foodmap[cell.x][cell.y][1] = Math.min(foodmap[cell.x][cell.y][1], 255);
                }
                cells1.remove(Integer.toString(cell.id));
            }
        }
        cells=cells1;
        for (Cell cell1: cells1.values()){//перераспределение энергии
            ArrayList<String> dead = new ArrayList<>();
            for(String kid: cell1.relations){
                if(cells1.keySet().contains(kid)){
                    Cell cell2=cells1.get(kid);
                    float sume = (cell1.energy+cell2.energy)/2;
                    cell1.energy = sume;
                    cell2.energy = sume;
                } else {//больше не обрабатываем мертвецов
                    dead.add(kid);
                }
            }
            for(String kid: dead){
                cell1.relations.remove(kid);
            }
        }
        cells=cells1;
        repaint();  // Перерисовываем экран

    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        System.out.println(key);
        if(key == 49){//управляем отображением
            display = 0;
        } else if(key == 50){
            display = 1;
        } else if(key == 51){
            display = 2;
        } else if(key==38){//приближение
            dsize += 0.25;
        } else if(key==40 && dsize > 0.25){
            dsize -= 0.25;
        } else if(key==87){//движение
            dy += 5;
        } else if(key==83){
            dy -= 5;
        } else if(key==65){
            dx += 5;
        } else if(key==68){
            dx -= 5;
        } else if(key==10){//enter - сохранение генома
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.*","*.*");
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(filter);
            if ( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
                try ( FileWriter fw = new FileWriter(fc.getSelectedFile()) ) {
                    for(int[] i: selectgenom) {
                        for (int j : i) {
                            fw.write(j+" ");
                        }
                        fw.write("\n");
                    }
                }
                catch ( IOException e2 ) {
                    System.out.println("lolidk");
                }
            }
        } else if(key==79){//o - чтение генома
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.*","*.*");
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(filter);
            if ( fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
                try ( FileReader fr = new FileReader(fc.getSelectedFile()) ) {
                    char[] buf = new char[256];
                    int c;
                    while((c = fr.read(buf))>0){
                        if(c < 256){
                            buf = Arrays.copyOf(buf, c);
                        }
                    }
                    String[] toparse = new String(buf).split("\n");
                    int x = 0;
                    for(String i: toparse){
                        int y = 0;
                        for(String j: i.split(" ")){
                            selectgenom[x][y] = Integer.parseInt(j);
                            y++;
                        }
                        x++;
                    }

                }
                catch ( IOException e2 ) {
                    System.out.println("lolidk");
                }
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    public void createCell(){
        Cell ncell = new Cell(mouseclickx, mouseclicky, rand.nextFloat(40,50), 0);
        ncell.genom = selectgenom;
        if(worldmap[ncell.x][ncell.y] != -1 && worldmap[ncell.x][ncell.y] != 3) {
            ncell.parent_id = maxid;
            ncell.world = this;
            ncell.id = maxid;
            ncell.lifetime = 1;
            cells.put(Integer.toString(maxid), ncell);
            maxid ++;
        }
    }
}