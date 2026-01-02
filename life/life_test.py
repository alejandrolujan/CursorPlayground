import unittest
from life import count_neighbors, parse_board_string, is_board_empty


class TestCountNeighbors(unittest.TestCase):
    """Unit tests for the count_neighbors function."""
    
    def test_no_neighbors(self):
        """Test a cell with no live neighbors."""
        board = [
            [0, 0, 0],
            [0, 1, 0],
            [0, 0, 0]
        ]
        self.assertEqual(count_neighbors(board, 1, 1), 0)
    
    def test_all_neighbors_alive(self):
        """Test a cell with all 8 neighbors alive."""
        board = [
            [1, 1, 1],
            [1, 0, 1],
            [1, 1, 1]
        ]
        self.assertEqual(count_neighbors(board, 1, 1), 8)
    
    def test_some_neighbors_alive(self):
        """Test a cell with some neighbors alive."""
        board = [
            [1, 0, 1],
            [0, 1, 0],
            [1, 0, 1]
        ]
        self.assertEqual(count_neighbors(board, 1, 1), 4)
    
    def test_top_left_corner(self):
        """Test top-left corner without wrapping."""
        board = [
            [0, 0, 1],
            [0, 0, 0],
            [1, 0, 0]
        ]
        # Cell at (0,0) - only checks neighbors within bounds:
        # (0,1)=0, (1,0)=0, (1,1)=0
        # Total: 0 neighbors
        self.assertEqual(count_neighbors(board, 0, 0), 0)
    
    def test_top_right_corner(self):
        """Test top-right corner without wrapping."""
        board = [
            [1, 0, 0],
            [0, 0, 0],
            [0, 0, 1]
        ]
        # Cell at (0,2) - only checks neighbors within bounds:
        # (0,1)=0, (1,1)=0, (1,2)=0
        # Total: 0 neighbors
        self.assertEqual(count_neighbors(board, 0, 2), 0)
    
    def test_bottom_left_corner(self):
        """Test bottom-left corner without wrapping."""
        board = [
            [1, 0, 0],
            [0, 0, 0],
            [0, 0, 1]
        ]
        # Cell at (2,0) - only checks neighbors within bounds:
        # (1,0)=0, (1,1)=0, (2,1)=0
        # Total: 0 neighbors
        self.assertEqual(count_neighbors(board, 2, 0), 0)
    
    def test_bottom_right_corner(self):
        """Test bottom-right corner without wrapping."""
        board = [
            [1, 0, 0],
            [0, 0, 0],
            [0, 0, 1]
        ]
        # Cell at (2,2) - only checks neighbors within bounds:
        # (1,1)=0, (1,2)=0, (2,1)=0
        # Total: 0 neighbors
        self.assertEqual(count_neighbors(board, 2, 2), 0)
    
    def test_top_edge(self):
        """Test a cell on the top edge."""
        board = [
            [0, 1, 0],
            [1, 0, 1],
            [0, 1, 0]
        ]
        # Cell at (0,1) - top edge, only checks neighbors within bounds:
        # (0,0)=0, (0,2)=0, (1,0)=1, (1,1)=0, (1,2)=1
        # Total: 2 neighbors
        self.assertEqual(count_neighbors(board, 0, 1), 2)
    
    def test_left_edge(self):
        """Test a cell on the left edge."""
        board = [
            [0, 1, 0],
            [1, 0, 1],
            [0, 1, 0]
        ]
        # Cell at (1,0) - left edge, only checks neighbors within bounds:
        # (0,0)=0, (0,1)=1, (1,1)=0, (2,0)=0, (2,1)=1
        # Total: 2 neighbors
        self.assertEqual(count_neighbors(board, 1, 0), 2)
    
    def test_center_cell(self):
        """Test a center cell in a larger board."""
        board = [
            [1, 0, 1, 0],
            [0, 1, 0, 1],
            [1, 0, 1, 0],
            [0, 1, 0, 1]
        ]
        # Cell at (1,1) - center
        # Expected neighbors: (0,0)=1, (0,1)=0, (0,2)=1, (1,0)=0, (1,2)=0, (2,0)=1, (2,1)=0, (2,2)=1
        # Total: 4 neighbors
        self.assertEqual(count_neighbors(board, 1, 1), 4)
    
    def test_small_board_2x2(self):
        """Test on a 2x2 board."""
        board = [
            [1, 1],
            [1, 0]
        ]
        # Cell at (1,1) - all 3 neighbors are alive
        # Expected neighbors: (0,0)=1, (0,1)=1, (1,0)=1
        # Total: 3 neighbors
        self.assertEqual(count_neighbors(board, 1, 1), 3)
    
    def test_small_board_2x2_corner(self):
        """Test corner cell on a 2x2 board without wrapping."""
        board = [
            [1, 1],
            [1, 0]
        ]
        # Cell at (0,0) - only checks neighbors within bounds:
        # (0,1)=1, (1,0)=1, (1,1)=0
        # Total: 2 neighbors
        self.assertEqual(count_neighbors(board, 0, 0), 2)
    
    def test_exactly_three_neighbors(self):
        """Test a cell with exactly 3 neighbors."""
        board = [
            [1, 1, 0],
            [1, 0, 0],
            [0, 0, 0]
        ]
        self.assertEqual(count_neighbors(board, 1, 1), 3)
    
    def test_exactly_two_neighbors(self):
        """Test a cell with exactly 2 neighbors."""
        board = [
            [1, 1, 0],
            [0, 0, 0],
            [0, 0, 0]
        ]
        self.assertEqual(count_neighbors(board, 1, 1), 2)


class TestParseBoardString(unittest.TestCase):
    """Unit tests for the parse_board_string function."""
    
    def test_valid_2x2_board(self):
        """Test parsing a valid 2x2 board."""
        board = parse_board_string("1010", 2)
        expected = [[1, 0], [1, 0]]
        self.assertEqual(board, expected)
    
    def test_valid_3x3_board(self):
        """Test parsing a valid 3x3 board."""
        board = parse_board_string("101010101", 3)
        expected = [[1, 0, 1], [0, 1, 0], [1, 0, 1]]
        self.assertEqual(board, expected)
    
    def test_all_zeros(self):
        """Test parsing a board with all zeros."""
        board = parse_board_string("0000", 2)
        expected = [[0, 0], [0, 0]]
        self.assertEqual(board, expected)
    
    def test_all_ones(self):
        """Test parsing a board with all ones."""
        board = parse_board_string("1111", 2)
        expected = [[1, 1], [1, 1]]
        self.assertEqual(board, expected)
    
    def test_single_cell_board(self):
        """Test parsing a 1x1 board."""
        board = parse_board_string("1", 1)
        expected = [[1]]
        self.assertEqual(board, expected)
    
    def test_complex_pattern(self):
        """Test parsing a complex pattern."""
        board = parse_board_string("110011001", 3)
        expected = [[1, 1, 0], [0, 1, 1], [0, 0, 1]]
        self.assertEqual(board, expected)
    
    def test_string_too_short(self):
        """Test that string too short raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("101", 2)
    
    def test_string_too_long(self):
        """Test that string too long raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("10101", 2)
    
    def test_empty_string(self):
        """Test that empty string raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("", 2)
    
    def test_invalid_character_letter(self):
        """Test that invalid character (letter) raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("10a0", 2)
    
    def test_invalid_character_number(self):
        """Test that invalid character (number other than 0/1) raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("1020", 2)
    
    def test_invalid_character_special(self):
        """Test that invalid character (special character) raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("10#0", 2)
    
    def test_invalid_character_space(self):
        """Test that invalid character (space) raises SystemExit."""
        with self.assertRaises(SystemExit):
            parse_board_string("10 0", 2)
    
    def test_4x4_board(self):
        """Test parsing a 4x4 board."""
        board = parse_board_string("1010101010101010", 4)
        expected = [[1, 0, 1, 0], [1, 0, 1, 0], [1, 0, 1, 0], [1, 0, 1, 0]]
        self.assertEqual(board, expected)
    
    def test_row_by_row_parsing(self):
        """Test that string is parsed row by row correctly."""
        # String "123456789" for 3x3 should be:
        # Row 0: 1,2,3
        # Row 1: 4,5,6
        # Row 2: 7,8,9
        board = parse_board_string("110011001", 3)
        # First row: positions 0-2
        self.assertEqual(board[0], [1, 1, 0])
        # Second row: positions 3-5
        self.assertEqual(board[1], [0, 1, 1])
        # Third row: positions 6-8
        self.assertEqual(board[2], [0, 0, 1])


class TestIsBoardEmpty(unittest.TestCase):
    """Unit tests for the is_board_empty function."""
    
    def test_empty_2x2_board(self):
        """Test that a 2x2 board with all zeros returns True."""
        board = [[0, 0], [0, 0]]
        self.assertTrue(is_board_empty(board))
    
    def test_empty_3x3_board(self):
        """Test that a 3x3 board with all zeros returns True."""
        board = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
        self.assertTrue(is_board_empty(board))
    
    def test_single_cell_zero(self):
        """Test that a single cell board with zero returns True."""
        board = [[0]]
        self.assertTrue(is_board_empty(board))
    
    def test_single_cell_one(self):
        """Test that a single cell board with one returns False."""
        board = [[1]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_with_one_cell_alive(self):
        """Test that a board with one live cell returns False."""
        board = [[0, 0, 0], [0, 1, 0], [0, 0, 0]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_with_multiple_cells_alive(self):
        """Test that a board with multiple live cells returns False."""
        board = [[1, 0, 1], [0, 1, 0], [1, 0, 1]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_all_ones(self):
        """Test that a board with all ones returns False."""
        board = [[1, 1], [1, 1]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_first_cell_alive(self):
        """Test that a board with first cell alive returns False."""
        board = [[1, 0, 0], [0, 0, 0], [0, 0, 0]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_last_cell_alive(self):
        """Test that a board with last cell alive returns False."""
        board = [[0, 0, 0], [0, 0, 0], [0, 0, 1]]
        self.assertFalse(is_board_empty(board))
    
    def test_board_middle_cell_alive(self):
        """Test that a board with middle cell alive returns False."""
        board = [[0, 0, 0], [0, 1, 0], [0, 0, 0]]
        self.assertFalse(is_board_empty(board))
    
    def test_large_empty_board(self):
        """Test that a large empty board returns True."""
        board = [[0] * 10 for _ in range(10)]
        self.assertTrue(is_board_empty(board))
    
    def test_large_board_with_one_alive(self):
        """Test that a large board with one alive cell returns False."""
        board = [[0] * 10 for _ in range(10)]
        board[5][5] = 1
        self.assertFalse(is_board_empty(board))
    
    def test_rectangular_empty_board(self):
        """Test that a rectangular empty board returns True."""
        board = [[0, 0, 0, 0], [0, 0, 0, 0]]
        self.assertTrue(is_board_empty(board))
    
    def test_rectangular_board_with_alive(self):
        """Test that a rectangular board with alive cell returns False."""
        board = [[0, 0, 0, 0], [0, 1, 0, 0]]
        self.assertFalse(is_board_empty(board))


if __name__ == '__main__':
    unittest.main()

