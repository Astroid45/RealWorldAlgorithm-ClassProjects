import java.util.Random;
import java.util.ArrayList;
import bridges.connect.Bridges;
import bridges.base.Color;
import bridges.base.ColorGrid;

public class Voronoi {
    public static void main(String[] args) throws Exception {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(19, username, apiKey);

        int gridSize = 750;
        int maxSites = 150;
        int siteSquareSize = 7; 

        
        for (int numSites = 2; numSites <= maxSites; numSites *= 2) {
            
            ArrayList<Point> sites = generateRandomSites(gridSize, gridSize, numSites);

            ColorGrid cg = new ColorGrid(gridSize, gridSize, new Color("lightgoldenrodyellow"));

            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    Point closestSite = findClosestSite(x, y, sites);
                    cg.set(x, y, closestSite.color);
                }
            }

            Color siteColor = new Color("black");
            Color siteCenterColor = new Color("white");
            for (Point site : sites) {
                cg.set((int) site.x, (int) site.y, siteColor);
                int startX = (int) (site.x - siteSquareSize / 2);
                int startY = (int) (site.y - siteSquareSize / 2);
                for (int i = startX; i < startX + siteSquareSize; i++) {
                    for (int j = startY; j < startY + siteSquareSize; j++) {
                        cg.set(i, j, siteCenterColor);
                    }
                }
            }

            bridges.setTitle("Voronoi Diagram - " + numSites + " Sites");
            bridges.setDescription("Voronoi diagram with " + numSites + " sites");
            bridges.setDataStructure(cg);
            bridges.visualize();
        }
    }
    public static ArrayList<Point> generateRandomSites(int width, int height, int numSites) {
        ArrayList<Point> sites = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numSites; i++) {
            double x = random.nextDouble() * width;
            double y = random.nextDouble() * height;
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            sites.add(new Point(x, y, color));
        }
        return sites;
    }
    public static Point findClosestSite(int x, int y, ArrayList<Point> sites) {
        double minDistance = Double.MAX_VALUE;
        Point closestSite = null;
        for (Point site : sites) {
            double distance = Math.sqrt(Math.pow(x - site.x, 2) + Math.pow(y - site.y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestSite = site;
            }
        }
        return closestSite;
    }
    static class Point {
        double x;
        double y;
        Color color;

        Point(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}
