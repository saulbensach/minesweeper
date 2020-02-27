ArrayList<Node> nodes = new ArrayList<Node>();
ArrayList<Node> delaunay = new ArrayList<Node>();
ArrayList<VCircle> voronoy = new ArrayList<VCircle>();

int NUM_POINTS = 50;
PVector cir = new PVector(0, 0);
float dir = 0.0;
float rot = 0.0;

int WIDTH = 800;
int HEIGHT = 800;

void setup() {
  size(800, 800);
  for (int i = 0; i < NUM_POINTS; i++) {
    float x = random(0, WIDTH);
    float y = random(0, HEIGHT);

    Node node = new Node(x, y);
    nodes.add(node);
  }

  delaunay_eval();
}

void delaunay_eval() {
  //Calculate delaunay using determinant evaluation
  // https://en.wikipedia.org/wiki/Delaunay_triangulation


  for (int i = 0; i < nodes.size(); i++) {
    for (int j = i+1; j < nodes.size(); j++) {
      for (int k = j+1; k < nodes.size(); k++) {
        Node a = nodes.get(i);
        Node b = nodes.get(j);
        Node c = nodes.get(k);
        boolean any = false;
        float al = a.pos.dist(b.pos);
        float bl = b.pos.dist(c.pos);
        float cl = c.pos.dist(a.pos);
        float up = 2*(al*bl*cl);
        float diameter = up / sqrt((al+bl+cl)*(-al+bl+cl)*(al-bl+cl)*(al+bl-cl));
        float D = 2*(a.pos.x*(b.pos.y - c.pos.y) + b.pos.x*(c.pos.y-a.pos.y) + c.pos.x*(a.pos.y-b.pos.y));

        float ox1 = ((a.pos.x * a.pos.x) + (a.pos.y * a.pos.y))*(b.pos.y - c.pos.y);
        float ox2 = ((b.pos.x * b.pos.x) + (b.pos.y * b.pos.y))*(c.pos.y - a.pos.y);
        float ox3 = ((c.pos.x * c.pos.x) + (c.pos.y * c.pos.y))*(a.pos.y - b.pos.y);
        float Ox = (ox1+ox2+ox3) / D;

        float oy1 = ((a.pos.x * a.pos.x) + (a.pos.y * a.pos.y))*(c.pos.x - b.pos.x);
        float oy2 = ((b.pos.x * b.pos.x) + (b.pos.y * b.pos.y))*(a.pos.x - c.pos.x);
        float oy3 = ((c.pos.x * c.pos.x) + (c.pos.y * c.pos.y))*(b.pos.x - a.pos.x);
        float Oy = (oy1+oy2+oy3) / D;
        PVector Oxy = new PVector(Ox, Oy);
        for (int l = 0; l < nodes.size(); l++) {
          if (l == i || l == j || l == k) continue;
          float dist = a.pos.dist(Oxy);
          Node d = nodes.get(l);
          if (d.pos.dist(Oxy) < dist) {
            any = true;
            break;
          }
        }
        if (!any) {
          Edge e1 = new Edge(a, b);
          Edge e2 = new Edge(a, c);
          Edge e3 = new Edge(b, c);
          a.edges.add(e1);
          a.edges.add(e2);
          b.edges.add(e3);
          VCircle vc = new VCircle(diameter, Oxy);
          voronoy.add(vc);
        }
      }
    }
  }
}

void draw() {
  background(0, 0, 0);

  for (Node n : nodes) {
    n.draw();
  }
  for (VCircle c : voronoy) {
    c.draw();
  }

  noFill();
  rot += 0.5;
}

abstract class Point {
  public PVector pos;
  public String name;

  public Point(float x, float y) {
    pos = new PVector(x, y);
  }

  public void draw() {
    stroke(255, 255, 255);
    point(pos.x, pos.y);
  }
}

class Node extends Point {

  public ArrayList<Edge> edges;

  public boolean visited = false;

  public Node(float x, float y) {
    super(x, y);
    edges = new ArrayList<Edge>();
  }

  void draw() {
    super.draw();
    for (Edge e : edges) {
      e.draw();
    }
  }
}

class VCircle {
  float diameter;
  PVector pos;
  
  public ArrayList<Edge> edges;

  public VCircle(float diameter, PVector pos) {
    this.diameter = diameter;
    this.pos = pos;
  }

  public void draw() {
    noFill();
    stroke(255, 0, 0);
    circle(pos.x, pos.y, diameter);
    fill(255, 0, 0);
    circle(pos.x, pos.y, 10);
  }
}

class Edge {
  public Node n1;
  public Node n2;

  public Edge(Node n1, Node n2) {
    this.n1 = n1;
    this.n2 = n2;
  }

  public void draw() {
    stroke(255);
    line(n1.pos.x, n1.pos.y, n2.pos.x, n2.pos.y);
  }
  public void draw(color c) {
    stroke(c);
    line(n1.pos.x, n1.pos.y, n2.pos.x, n2.pos.y);
  }
}
