import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListen extends MouseAdapter {
    SimpleGame world;
    Cell select;
    public MouseListen(SimpleGame world){
        this.world = world;
    }
    public void mouseClicked(MouseEvent e) {
        world.mouseclickx = (int) Math.floor((e.getX() / world.dsize - world.dx - 1) / 5);
        world.mouseclicky = (int) Math.floor((e.getY() / world.dsize - world.dy - 1) / 5);
        switch (e.getButton()) {
            case 1:
                if (world.cells.containsKey(Integer.toString(world.cellmap[world.mouseclickx][world.mouseclicky]))) {
                    select = world.cells.get(Integer.toString(world.cellmap[world.mouseclickx][world.mouseclicky]));
                    world.selectgenom = select.genom;
                }
            break;
            case 3:
                world.createCell();
        }
    }
}
