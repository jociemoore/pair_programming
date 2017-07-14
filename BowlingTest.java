import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class BowlingTest {

	private Bowling bowling;

	@Before 
	public void setup() {
		bowling = new Bowling();
	}

	@Test
	public void shouldOutputAScoreGivenOneFrame() throws Exception {
		bowling.bowl(2);
		bowling.bowl(3);
		int score = bowling.score();
		assertEquals(5, score);
	}

	@Test
	public void shouldOutputScoreForEmptyGame() throws Exception {
		bowling.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0);

		int score = bowling.score();
		assertEquals(0, score);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenPinsDownIsNegative() throws Exception {
		try {
			bowling.bowl(-1);
			fail("Illegal Argument Exception was not thrown for bowling negative pins.");
		} catch (IllegalArgumentException iae) {
			String message = "Cannot knock down negative pins. Bowl again.";
			assertEquals(message, iae.getMessage());
		}
	}
	
	@Test
	public void shouldNotTotalScoreForAFrameWithASpare() throws Exception {
		bowling.bowl(5).bowl(5);
		
		int score = bowling.score();
		
		assertEquals(0, score);
	}

	@Test
	public void shouldOutputScoreOfPreviousFramesAfterASpare() throws Exception {
		bowling.bowl(2).bowl(7)
			.bowl(4).bowl(2)
			.bowl(5).bowl(5);

		int score = bowling.score();

		assertEquals(15, score);
	}

	@Test
	public void shouldIncludeSpareScoreAfterNextNonSpareNonStrikeFrame() throws Exception {
		bowling.bowl(3).bowl(5)
			.bowl(3).bowl(7)
			.bowl(4).bowl(0);

		int score = bowling.score();

		assertEquals(26, score);
	}

}
