import java.util.*;

public class Bowling {

  private int totalPoints;

  public Bowling() {
    totalPoints = 0; 
  }

  public Bowling bowl(int pinsDown) {
		if (pinsDown < 0) {
			throw new IllegalArgumentException("Cannot knock down negative pins. Bowl again.");
		}
    totalPoints += pinsDown;
    return this;
  }

  public int score() {
    if (totalPoints == 10) {
        return 0;
    } else 
        return totalPoints;
  }

}
