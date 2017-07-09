import java.util.*;

public class Bowling {

  private int totalPoints;

  public Bowling() {
    totalPoints = 0; 
  }

  public Bowling bowl(int pinsDown) {
    totalPoints += pinsDown;
    return this;
  }

  public int score() {
    return totalPoints;
  }

}
