class CryptoSquare
  def initialize(string)
    @english = string.downcase
  end

  def encode
    normalized_chars = @english.chars.select do |char|
      char if char =~ /[a-z0-9]/
    end
    grid_width = Math.sqrt(normalized_chars.size).ceil
    phrases = Array.new(grid_width, String.new)

    normalized_chars.each_slice(grid_width) do |row| 
      row.each_with_index do |char, index|
        phrases[index] += char.to_s
      end
    end

    phrases.join(" ")
  end
end



# ====================================================


# class CryptoSquare
#   def initialize(string)
#     @english = string.downcase
#   end

#   def encode
#     normalized_chars = english.chars.select do |char|
#       char if char =~ /[a-z0-9]/
#     end
#     grid_width = Math.sqrt(normalized_chars.size).ceil
    
#     square = square_up_sentence(grid_width, normalized_chars)

#     generate_output(square)
#   end

#   private

#   def square_up_sentence(grid_width, normalized_chars)
#     index = 0
#     square = []
#     while (index < normalized_chars.size) do
#       square << normalized_chars.slice(index, grid_width)
#       index += grid_width
#     end

#     square
#   end

#   def generate_output(square)
#     output = ""
#     (0...square[0].size).each do |i|
#       square.each do |line|
#         output += line[i].to_s
#       end
#       output += " "
#     end
#     output.chomp(' ')
#   end

#   attr_reader :english
# end








