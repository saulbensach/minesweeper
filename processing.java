int CELL_SIZE = 20;
int WIDTH = 800;
int HEIGHT = 800;
int mines = 200;
ArrayList<Cell> cells = new ArrayList<Cell>();

void setup() {
  size(800, 800);
  
  // Generate Grid
  int index = 0;
  for (int y = 0; y < WIDTH / CELL_SIZE; y++) {
    for (int x = 0; x < HEIGHT / CELL_SIZE; x++) {
      if (y == 0)continue;
      if (x == 0)continue;
      if (x == WIDTH / CELL_SIZE - 1)continue;
      if (y == HEIGHT / CELL_SIZE -1)continue;

      cells.add(new Cell(x, y, index++));
    }
  }
  
  //Place Miness bruteforce
  int mines_left = mines;
  while (mines_left != 0) {
    index = (int) random(0, cells.size() - 1);
    Cell cell = cells.get(index);
    if (!cell.mine) {
      cell.mine = true;
      mines_left--;
    }
  }
}

void draw() {
  background(111, 111, 111);

  for (int i = 0; i < cells.size(); i++) {
    cells.get(i).draw();
  }
}

void mouseClicked() {
  // find the cell that has been clicked
  int clickX = mouseX;
  int clickY = mouseY;

  for (int i = 0; i < cells.size(); i++) {
    if (cells.get(i).contains(clickX, clickY)) {
      Cell cell = cells.get(i);
      cell.clicked = true;
      cell.neighbours(cell);
      break;
    }
  }
}

class Cell {
  public int x;
  public int y;
  public int index;
  public boolean clicked = false;
  public boolean mine = false;
  public int near_bombs = 0;

  private boolean show_mines = true;

  private int size = CELL_SIZE;
  private color idle = color(255, 0, 0);
  private color pressed = color(0, 255, 0);
  private color b_color = color(0, 0, 255);

  public Cell(int x, int y, int index) {
    this.x = x;
    this.y = y;
    this.index = index;
  }

  public boolean contains(int x, int y) {
    if (clicked) return false;
    if (x >= this.x * size 
      && x <= (this.x * size) + size
      && y >= this.y * size 
      && y <= (this.y * size) + size) 
      return true;

    return false;
  }

  // Performs a floor fill algorithm until
  // a cell constains bombs nearby
  public void neighbours(Cell current) {
    current.clicked = true;
    int cols = (WIDTH / CELL_SIZE) - 2;
    int rows = (HEIGHT / CELL_SIZE) - 2;
    int x_pos = current.index % cols;
    int y_pos = (current.index % cells.size()) / rows;
    int bombs = 0;

    ArrayList<Cell> n_cells = new ArrayList<Cell>();

    for (int yy = -1; yy <= 1; yy++) {
      for (int xx = -1; xx <= 1; xx++) {
        if (xx + x_pos == x_pos && yy + y_pos == y_pos) continue;
        if (xx + x_pos >= 0 
          && xx + x_pos < cols 
          && yy + y_pos >= 0 
          && yy + y_pos < rows) {
          int n_index = (x_pos + xx) + ((yy+y_pos) * cols);
          Cell cell = cells.get(n_index);

          if (cell.mine) {
            bombs++;
          } else {
            n_cells.add(cell);
          }
        }
      }
    }
    if (bombs < 1) {
      for (Cell cell : n_cells) {
        if (!cell.clicked) {
          neighbours(cell);
        }
      }
    }
    current.near_bombs = bombs;
  }

  public void draw() {
    fill((clicked ? pressed : idle));
    rect(x * size, y * size, size, size);
    int text_size = WIDTH/35;

    if ((mine && clicked) || (show_mines && mine)) {
      textSize(text_size);
      fill(b_color);
      text("B", x * size + size/2 - 5, (y * size) + size - size/2 + 9);
    }
    if (near_bombs > 0) {
      textSize(text_size);
      fill(b_color);
      text("" + near_bombs, x * size + size/2 - 5, (y * size) + size - size/2 + 9);
    }
  }
}
