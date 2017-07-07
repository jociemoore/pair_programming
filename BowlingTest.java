import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class BowlingTest {

	private Bowling bowling;

	@Before 
	public void setup() {
		bowling = new Bowling();
	}

	@Test
	public void shouldOutputAScoreGivenOneTurn() throws Exception {
		bowling.bowl(5);
		int score = bowling.score();
		assertEquals(5, score);
	}

	@Test
	public void shouldOutputScoreForEmptyGame() throws Exception {
		bowling.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0)
			.bowl(0);

		int score = bowling.score();
		assertEquals(0, score);
	}

}
