class Bst # :nodoc:
  def initialize
    @top_node
  end

  def insert(number)
    new_node = Node.new(number)
    if top_node.nil?
      self.top_node = new_node
    elsif search(number)
      puts "The number #{number} already exists in the BST."
    else
      top_node.add(new_node)
    end
    self
  end

  def search(number)
    top_node.find(number)
  end

  # private - public for testing
  attr_accessor :top_node
end

class Node # :nodoc:
  def initialize(number)
    @left
    @right
    @value = number
  end

  def add(new_node)
    if new_node.value < value
      left.nil? ? self.left = new_node : left.add(new_node)
    else
      right.nil? ? self.right = new_node : right.add(new_node)
    end
  end

  def find(number)
    if current_value_matches(number)
      true
    elsif end_of_branch
      false
    else
      keep_looking(number, value)
    end
  end

  # protected -- made public for testing
  attr_accessor :left, :right, :value

  private

  def current_value_matches(number)
    value == number
  end

  def end_of_branch
    left.nil? && right.nil?
  end

  def keep_looking(number, current_value)
    if number < current_value && !left.nil?
      left.find(number)
    elsif number > current_value && !right.nil?
      right.find(number)
    else
      false
    end
  end
end
