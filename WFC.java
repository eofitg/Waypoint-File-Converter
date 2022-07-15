import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class WFC {

    //private Frame f;
    private TextArea ted;
    private final JFrame f;
    private final ArrayList<Point> points = new ArrayList<>();
    public WFC() {

        //f = new Frame("Waypoint File Converter (By eofitg)");
        f = new JFrame("Waypoint File Converter (By eofitg)");


        //f.setBounds(500, 400, 800, 300);
        int width = 940;
        int height = 529;
        Dimension d = new Dimension(width, height);
        f.setSize(d);

        int windowWidth = f.getWidth();                     //获得窗口宽
        int windowHeight = f.getHeight();                   //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
        Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
        int screenWidth = screenSize.width;                     //获取屏幕的宽
        int screenHeight = screenSize.height;                   //获取屏幕的高
        f.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2); //设置窗口居中显示
        f.setResizable(false); //禁止调整窗口大小

        //Menu无法直接添加到容器中，只能直接添加到菜单容器中
        MenuBar mb = new MenuBar(); //创建菜单容器
        f.setMenuBar(mb);

        //生成背景图
        Random r = new Random();
        int i = r.nextInt(7);
        ImageIcon image1 = new ImageIcon("Background/bg"+i+".png");
        JLabel label = new JLabel(image1);
        label.setToolTipText("!NOTHING HERE!");
        f.add(label);

        //添加菜单
        Menu file = new Menu("File");
        Menu load = new Menu("Load");
        Menu build = new Menu("Build");
        Menu help = new Menu("Help");
        mb.add(file);
        mb.add(load);
        mb.add(build);
        mb.add(help);

        //添加菜单项
        MenuItem buildXaero = new MenuItem("Build new XaerosMiniMap points directory");
        MenuItem buildVoxel = new MenuItem("Build new VoxelMap points file");
        MenuItem loadXaero = new MenuItem("Load new XaerosMiniMap points directory");
        MenuItem loadVoxel = new MenuItem("Load new VoxelMap points file");
        MenuItem quit = new MenuItem("Quit");

        //file.addSeparator(); //添加分隔线
        file.add(quit);
        load.add(loadXaero);
        load.add(loadVoxel);
        build.add(buildXaero);
        build.add(buildVoxel);

        //添加监听
        quit.addActionListener(new MenuListener());
        loadXaero.addActionListener(new MenuListener());
        loadVoxel.addActionListener(new MenuListener());
        buildXaero.addActionListener(new MenuListener());
        buildVoxel.addActionListener(new MenuListener());

        f.setVisible(true);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });

    }

    class MenuListener implements ActionListener{ //活动监听

        @Override
        public void actionPerformed(ActionEvent e) {
            // 操作的组件是谁，就返回谁
            MenuItem i = (MenuItem) e.getSource();
            if("Quit".equals(i.getLabel())) {
                System.exit(1);
            } else if("Build new XaerosMiniMap points directory".equals(i.getLabel())) {
                buildXaeroFile();
            } else if("Build new VoxelMap points file".equals(i.getLabel())) {
                buildVoxelFile();
            } else if("Load new XaerosMiniMap points directory".equals(i.getLabel())) {
                loadXaeroFile();
            } else if("Load new VoxelMap points file".equals(i.getLabel())) {
                loadVoxelFile();
            }
        }

    }

    static class Point{ //路径点信息
        String name = ""; //名字
        String shortName = ""; //缩写，仅作用于Xaero
        boolean show; //是否显示，遵循Voxel定义方法：true为显示，false为隐藏（Xaero正好相反）
        int color = 0; //颜色，只在Xaero->Voxel单向转换时有作用
        int x, y, z; //三维坐标
        int dim; //维度，遵循Xaero定义方法：1为末地，0为主世界，-1为下届（注：当VoxelMap的路径点跨纬度显示打开时这一信息将失效）
        void print(){
            System.out.println("name:"+name+"\n");
            System.out.println("shortName:"+shortName+"\n");
            System.out.println("show:"+show+"\n");
            System.out.println("color:"+color+"\n");
            System.out.println("x, y, z:"+x+','+y+','+z+"\n");
            System.out.println("dim:"+dim+"\n");
        }
    }

    /**
     * 创建转换后的路径点文件
     */
    void buildXaeroFile() {

        if(points.isEmpty()){
            JOptionPane.showMessageDialog(null, "You haven't loaded the points file yet!", "Failed to build", JOptionPane.ERROR_MESSAGE);
        }
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.showDialog(new JLabel(), "Please select a folder to build");
        File file=jfc.getSelectedFile();
        String fileName = "/XaerosMiniMap";
        String filePath = "";

        //检验该路径导向的是否是文件夹
        if(file.isDirectory()){
            filePath = file.getAbsolutePath() + fileName;
            //System.out.println(fileName+' '+filePath);
        }else if(file.isFile()){
            JOptionPane.showMessageDialog(null, "Build file failed! Please select a folder!", "Failed to build", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            //设置维度分文件夹
            String pathTheEnd = filePath + "/dim%1/mw$default_2.txt";
            String pathOverworld = filePath + "/dim%0/mw$default_2.txt";
            String pathTheNether = filePath + "/dim%-1/mw$default_2.txt";
            makedir(pathTheEnd);
            makedir(pathOverworld);
            makedir(pathTheNether);
            //设置对应IO流
            FileOutputStream fosTheEnd = new FileOutputStream(pathTheEnd);
            FileOutputStream fosOverworld = new FileOutputStream(pathOverworld);
            FileOutputStream fosTheNether = new FileOutputStream(pathTheNether);
            OutputStreamWriter oswTheEnd = new OutputStreamWriter(fosTheEnd);
            OutputStreamWriter oswOverworld = new OutputStreamWriter(fosOverworld);
            OutputStreamWriter oswTheNether = new OutputStreamWriter(fosTheNether);
            //提前输出前缀数据
            oswTheEnd.write("#\n#waypoint:name:initials:x:y:z:color:disabled:type:set:rotate_on_tp:tp_yaw:global\n#\n");
            oswOverworld.write("#\n#waypoint:name:initials:x:y:z:color:disabled:type:set:rotate_on_tp:tp_yaw:global\n#\n");
            oswTheNether.write("#\n#waypoint:name:initials:x:y:z:color:disabled:type:set:rotate_on_tp:tp_yaw:global\n#\n");
            for (Point point : points) {
                switch (point.dim) {
                    case 1 -> writeLine(point, oswTheEnd);
                    case 0 -> writeLine(point, oswOverworld);
                    case -1 -> writeLine(point, oswTheNether);
                }
            }
            oswTheNether.flush();
            oswTheNether.close();
            oswOverworld.flush();
            oswOverworld.close();
            oswTheEnd.flush();
            oswTheEnd.close();
            fosTheNether.close();
            fosOverworld.close();
            fosTheEnd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "Build succeeded", "", JOptionPane.INFORMATION_MESSAGE);

    }
    void buildVoxelFile() {

    }

    /**
     * 导入将被转换的路径点文件
     */
    void loadXaeroFile() {

    }
    void loadVoxelFile() {

        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //jfc.setFileFilter(MyFileFilter); //呜呜呜不会写文件过滤
        jfc.showDialog(new JLabel(), "Please select a .points file to load");
        File file=jfc.getSelectedFile();
        String fileName = "";
        String filePath = "";

        //检验该路径导向的是否是文件
        if(file.isDirectory()){
            //System.out.println("文件夹:"+file.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "Failed to load folder! Please select a .points file!", "Failed to load", JOptionPane.ERROR_MESSAGE);
            return;
        }else if(file.isFile()){
            //检验是否是points文件
            //System.out.println("文件:"+file.getAbsolutePath());
            fileName = file.getName();
            filePath = file.getAbsolutePath();
            //System.out.println(filePath);
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if(!suffix.equals("points")) {
                JOptionPane.showMessageDialog(null, "The file type could not be loaded! Please select a .points file!", "Failed to load", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if(!points.isEmpty()){ //清除历史数据
            points.clear();
        }
        //System.out.println(jfc.getSelectedFile().getName());
        //ted.setText("");
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line = "";
            while ((line = in.readLine()) != null)
            {
                //ted.setText(ted.getText() + line+ System.getProperty("line.separator"));
                if(line.equals("subworlds:")||line.equals("oldNorthWorlds:")||line.equals("seeds:")){
                    continue;
                }
                /*
                 * VoxelMap数据存储基本单元:
                 * name:home,                                       (名字)
                 * x:-355,z:80,y:106,                               (坐标)
                 * enabled:true,                                    (是否显示)
                 * red:0.6756376,green:0.8263382,blue:0.6491943,    (RGB颜色)
                 * suffix:,world:,                                  (暂时不管)
                 * dimensions:overworld#                            (当且仅当只有一个#时为有效维度数据)
                 */
                //收集本行的有效数据
                Point temp = new Point();
                String[] arr = line.split(",");
                temp.name = arr[0].split(":")[1];
                temp.shortName = temp.name.substring(0,1).toUpperCase();
                temp.x = Integer.parseInt(arr[1].split(":")[1]);
                temp.z = Integer.parseInt(arr[2].split(":")[1]);
                temp.y = Integer.parseInt(arr[3].split(":")[1]);
                temp.show = Boolean.parseBoolean(arr[4].split(":")[1]);
                Random r = new Random();
                temp.color = r.nextInt(15); //偷懒,以后等我弄清颜色通道了可能会改(画饼)
                switch (arr[10].split(":")[1]) {
                    case "overworld#" -> temp.dim = 0;
                    case "the_end#" -> temp.dim = 1;
                    case "the_nether#" -> temp.dim = -1;
                }
                //temp.print();
                points.add(temp);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "Loaded successfully", "", JOptionPane.INFORMATION_MESSAGE);

    }

    void makedir(String filePath) {
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeLine(Point p, OutputStreamWriter o) throws IOException {
        /*
         * XaerosMiniMap数据存储基本单元:
         * waypoint:xjt:                                       (名字)
         * X:                                                  (缩写)
         * -363:128:15:                                        (坐标)
         * 7:                                                  (颜色)
         * false:                                              (是否隐藏)
         * 0:gui.xaero_default:false:0:false                   (暂且不管)
         */
        o.write("waypoints:" + p.name + ":");
        o.write(p.shortName + ":");
        o.write(p.x + ":" + p.y + ":" + p.z + ":");
        o.write(p.color + ":");
        o.write(!p.show + ":");
        o.write("0:gui.xaero_default:false:0:false\n");
    }

    public static void main(String[] args) {
        WFC md = new WFC();
    }

}