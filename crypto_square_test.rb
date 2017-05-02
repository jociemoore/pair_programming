require 'minitest/autorun'
require_relative 'crypto_square'

class CryptoTest < Minitest::Test
  def test_should_encode_2_by_2_grid
    crypto_square = CryptoSquare.new("it is")
    assert_equal "ii ts", crypto_square.encode
  end

  def test_should_normalize_capitalization
    crypto_square = CryptoSquare.new("Hi It")
    assert_equal "hi it", crypto_square.encode
  end

  def test_should_normalize_punctuation
    crypto_square = CryptoSquare.new("I'm it")
    assert_equal "ii mt", crypto_square.encode
  end

  def test_should_handle_larger_grid
    crypto_square = CryptoSquare.new("Her red car")
    assert_equal "hrc eea rdr", crypto_square.encode
  end

  def test_should_handle_non_square_grid
    crypto_square = CryptoSquare.new("Her red in")
    assert_equal "hri een rd", crypto_square.encode
  end

  def test_should_handle_where_last_line_of_grid_is_empty
    crypto_square = CryptoSquare.new("Her car")
    assert_equal "hc ea rr", crypto_square.encode
  end

  def test_should_handle_one_liner
    crypto_square = CryptoSquare.new("if")
    assert_equal "i f", crypto_square.encode
  end

  def test_should_handle_a_larger_grid
    crypto_square = CryptoSquare.new("If man was meant to stay on the ground god would have given us roots")
    assert_equal "imtgdvs fearwer mayoogo anouuio ntnnlvt wttddes aohghn sseoau", crypto_square.encode
  end
end