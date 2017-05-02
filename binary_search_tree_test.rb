require 'minitest/autorun'
require 'minitest/reporters'

Minitest::Reporters.use!

require_relative 'binary_search_tree'

class BstTest < Minitest::Test
  def test_should_insert_into_correct_position_of_small_tree
    tree = Bst.new.insert(5).insert(4)
    assert_equal(4, tree.top_node.left.value)
  end

  def test_should_insert_into_correct_position_of_large_tree
    tree = Bst.new.insert(5).insert(4).insert(3).insert(6)
    assert_equal(6, tree.top_node.right.value)
    assert_equal(3, tree.top_node.left.left.value)
  end

  def test_should_return_true_for_left_existing_value
    tree = Bst.new.insert(5).insert(4)
    assert_equal(true, tree.search(4))
  end

  def test_should_return_true_for_right_existing_value
    tree = Bst.new.insert(5).insert(6)
    assert_equal(true, tree.search(6))
  end

  def test_should_return_false_for_nonexisting_value
    tree = Bst.new.insert(5).insert(6)
    assert_equal(false, tree.search(4))
  end

  def test_should_return_true_for_existing_left_left_value
    tree = Bst.new.insert(5).insert(4).insert(2)
    assert_equal(true, tree.search(2))
  end

  def test_should_return_true_for_existing_right_right_value
    tree = Bst.new.insert(5).insert(6).insert(9)
    assert_equal(true, tree.search(9))
  end

  def test_should_return_true_for_existing_left_right_value
    tree = Bst.new.insert(5).insert(2).insert(3)
    assert_equal(true, tree.search(3))
  end

  def test_should_return_true_for_existing_two_branches
    tree = Bst.new.insert(5).insert(2).insert(6).insert(7)
    assert_equal(true, tree.search(7))
  end

  def test_should_return_true_for_existing_value_large_tree
    tree = Bst.new.insert(5).insert(2).insert(3).insert(10).insert(7).insert(8).insert(9)
    assert_equal(true, tree.search(9))
  end

  def test_should_not_insert_value_if_already_exists
    tree = Bst.new.insert(5).insert(2).insert(3).insert(2)
    assert_nil(tree.top_node.left.right.left)
  end

  def test_should_return_message_if_inserting_existing_value
    tree = Bst.new.insert(5).insert(2).insert(3)
    assert_output(/The number 2 already exists in the BST./) {tree.insert(2)}
  end
end
