import java.io.*;
import java.util.Collections;
import java.util.Vector;


/**
 * 
 * @author Loïc SERRE, TP1
 */

/** Principal class*/
public class tp1 {

    /** Classe Point who contains x and y coordinates of a point */
    class Point{
        int x;
        int y;
    }

    static String coord = ""; // Raw SVG File Coordinates
    static String svgheader = ""; // Header of the SVG File
    static String svgfooter1 = ""; // Footer of the SVG File before the size
    static String svgfooter2 = ""; // Footer of the SVG File after the size
    static double size = 0;  // Size of the path
    static int nbcroisement = 0; // Number of crossing
    static Vector<Point> points = new Vector<Point>(); // Vector of points

    /** Retrieve all info from an SVG file (header, coordinates, footer, size) */
    public static void getinfo(String namefile) throws FileNotFoundException {
        File f = new File(namefile);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String tmp = "";
        String line = "";
        try{
            while ((line = br.readLine()) != null )
                tmp += line;
        }catch (IOException e) {
            e.printStackTrace();
        }

        // Get the header of the SVG File
        svgheader = tmp.substring(0, tmp.indexOf("points"));
        svgheader += "points=\"";

        // Get the coordinates of the SVG File
        coord = tmp.substring(tmp.indexOf("points"));
        coord = coord.substring(coord.indexOf("\""));
        coord = coord.replace("\"", "");
        coord = coord.substring(0, coord.indexOf("/"));

        // Split coordinates based on spaces
        coord = " " + coord;
        String[] coords = coord.split(" ");

        // Putting coordinates into a vector of points
        tp1 t = new tp1();
        Point p = t.new Point();
    
        for (int i = 1; i < coords.length; i++){ // Start at 1 because the first element is a space
            p = t.new Point();
            p.x = Integer.parseInt(coords[i].substring(0, coords[i].indexOf(","))); // Retrieve x coordinate
            p.y = Integer.parseInt(coords[i].substring(coords[i].indexOf(",") + 1)); // Retreve y coordinate
            points.add(p);
        }

        // Get the footer of the SVG File before the size
        svgfooter1 = tmp.substring(tmp.indexOf("<text"));
        svgfooter1 = svgfooter1.substring(0, svgfooter1.indexOf("Length: "));
        svgfooter1 += "Length: ";
        svgfooter1 = "\"/>" + svgfooter1;

        // Get the footer of the SVG File after the size
        int index = tmp.indexOf("Length: ");
        svgfooter2 = tmp.substring(index + 8);
        svgfooter2 = svgfooter2.substring(svgfooter2.indexOf("<"));

        // Get the size of the path
        String strsize = "";
        strsize = tmp.substring(tmp.indexOf("Length: "));
        strsize = strsize.substring(8);
        strsize = strsize.substring(0, strsize.indexOf("<"));
        size = Double.parseDouble(strsize);
    }

    /** Calculate path size */
    public static double calculdistance(){
        size = 0;

        for (int i = 0; i < points.size() - 1; i++) // Parcours du vecteur de points
            size += distance(points.get(i), points.get(i + 1)); // Ajout de la distance entre deux points
        size += distance(points.get(points.size() - 1), points.get(0)); // Ajout de la distance entre le dernier point et le premier
    
        return size;
    }

    /** Calculate distance between two points */
    public static double distance(Point a, Point b){
        // Calcul de la distance entre deux points
        return Math.abs(Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2))); 
    }

    /** Algorithm of the path optimization */
    public static void sortarrete() { 
        boolean before = false;

        // While there is still crossing
        while(!before){
            before = true;
            for (int i = 0; i < points.size()-2; i++) { // Dots vector path
                for (int j = i+2; j < points.size(); j++) {
                    if (((j+1)% points.size() != i) && iscrossing(points.get(i), points.get(i + 1), points.get(j), points.get((j + 1)%points.size()))){ // If there is a crossing
                        Collections.reverse(points.subList(i+1, j+1)); // Reverse the path between the two crossing points
                        before = false; // There is still crossing
                        nbcroisement++;
                    }                    
                }
            }
        }
    }
    
    /** Test of orientation of three points */
    public static boolean orientation(Point a, Point b, Point c){
        // If the orientation is positive then the three points are in the same direction
        return ((a.x*b.y) + (b.x*c.y) + (c.x*a.y) - (a.y*b.x) - (b.y*c.x) - (c.y*a.x) > 0) ? true : false;
    }

    /** Test of crossing between two edges */
    public static boolean iscrossing(Point p1, Point p2, Point p3, Point p4) { 
        // If the orientation of the two edges are different then there is a crossing
        return (orientation(p1, p2, p3) != orientation(p1, p2, p4)) && (orientation(p3, p4, p1) != orientation(p3, p4, p2) ? true : false);
    }

    /** Write a SVG file */
    public static void write(String namefile) throws FileNotFoundException {
        File f = new File(namefile);
        PrintWriter pw = new PrintWriter(f);

        coord = ""; // Reset the coordinates
        for (int i = 0; i < points.size(); i++) // Dots vector path
            coord += points.get(i).x + "," + points.get(i).y + " "; // Add the coordinates of the points
        pw.println(svgheader + coord + svgfooter1 + calculdistance() + svgfooter2); // Write the SVG file
        
        pw.close(); // Close the file
    }

    /** Display information (path length, number of crossings, execution time) */
    public static void main(String[] args) throws FileNotFoundException{
        double lastsize = 0; // Last path length

        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.print("\t\t\t\tLoïc SERRE \t TP1\n" + 
        "\t\t\t\t\t\t s21213416\n" + 
        "\t\t\t\t\t\t 10/02/2023\n"); 

        System.out.println("\n----------------------------------------------------------------------------------\n");
        System.out.print("\t\t\t\tProgram start\n\n");
        System.out.println("----------------------------------------------------------------------------------\n");

        //-----------------------------------------------s_x.svg------------------------------------------------

        System.out.println("-----------------------------------------s_x-----------------------------------------");
        System.out.println("Entry file : s_x.svg");
        System.out.println("Output file : s_x_sorted.svg\n");

        long startTime = System.nanoTime(); // Start of the chronometer
        
        getinfo("./File/s_x.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/s_x_sorted.svg"); // Write the SVG file 
        
        long endTime = System.nanoTime(); // End of the chronometer
        
        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        // Reset variables for the next file, else the program will not work
        points.clear();
        coord = "";
        svgheader = "";
        svgfooter1 = "";
        svgfooter2 = "";
        nbcroisement = 0;

        //-----------------------------------------------s_rnd.svg------------------------------------------------

        System.out.println("\n-----------------------------------------s_rnd-----------------------------------------\n");
        System.out.println("Entry file : s_rnd.svg");
        System.out.println("Output file : s_rnd_sorted.svg\n");

        startTime = System.nanoTime(); // Start of the chronometer

        getinfo("./File/s_rnd.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/s_rnd_sorted.svg"); // Write the SVG file

        endTime = System.nanoTime(); // End of the chronometer

        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        // Reset variables for the next file, else the program will not work
        points.clear();
        coord = "";
        svgheader = "";
        svgfooter1 = "";
        svgfooter2 = "";
        nbcroisement = 0;

        System.out.println("\n-----------------------------------------m_x-----------------------------------------\n");
        System.out.println("Entry file : m_x.svg");
        System.out.println("Output file : m_x_sorted.svg\n");

        startTime = System.nanoTime(); // Start of the chronometer

        getinfo("./File/m_x.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/m_x_sorted.svg"); // Write the SVG file

        endTime = System.nanoTime(); // End of the chronometer

        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        // Reset variables for the next file, else the program will not work
        points.clear();
        coord = "";
        svgheader = "";
        svgfooter1 = "";
        svgfooter2 = "";
        nbcroisement = 0;

        System.out.println("\n-----------------------------------------m_rnd-----------------------------------------\n");
        System.out.println("Entry file : m_rnd.svg");
        System.out.println("Output file : m_rnd_sorted.svg\n");

        startTime = System.nanoTime(); // Start of the chronometer

        getinfo("./File/m_rnd.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/m_rnd_sorted.svg"); // Write the SVG file

        endTime = System.nanoTime(); // End of the chronometer

        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        // Reset variables for the next file, else the program will not work
        points.clear();
        coord = "";
        svgheader = "";
        svgfooter1 = "";
        svgfooter2 = "";
        nbcroisement = 0;

        System.out.println("\n-----------------------------------------l_x-----------------------------------------\n");
        System.out.println("Entry file : l_x.svg");
        System.out.println("Output file : l_x_sorted.svg\n");

        startTime = System.nanoTime();  // Start of the chronometer

        getinfo("./File/l_x.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/l_x_sorted.svg"); // Write the SVG file

        endTime = System.nanoTime(); // End of the chronometer

        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        // Reset variables for the next file, else the program will not work
        points.clear();
        coord = "";
        svgheader = "";
        svgfooter1 = "";
        svgfooter2 = "";
        nbcroisement = 0;

        System.out.println("\n-----------------------------------------l_rnd-----------------------------------------\n");
        System.out.println("Entry file : l_rnd.svg");
        System.out.println("Output file : l_rnd_sorted.svg\n");

        startTime = System.nanoTime(); // Start of the chronometer

        getinfo("./File/l_rnd.svg"); // Get the information of the SVG file
        lastsize = calculdistance(); // Calculate the last path length
        sortarrete(); // Sort the edges to uncross them
        write("./File/l_rnd_sorted.svg"); // Write the SVG file

        endTime = System.nanoTime(); // End of the chronometer

        System.out.println("Execution time : " + (endTime - startTime)/1000000 + "ms");
        System.out.println("Number of crossing : " + nbcroisement);
        System.out.println("Size of the path before uncrossing :  " + lastsize);
        System.out.println("Size of the path after uncrossing : " + size);

        System.out.println("\n----------------------------------------------------------------------------------\n");
        System.out.println("\t\t\t\tFin du programme\n");
        System.out.println("----------------------------------------------------------------------------------\n");
    }
}
