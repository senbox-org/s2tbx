#!/usr/bin/env python

#
# Requirements : python-openpyxl
#
# - VENV=l2benv
# - virtualenv $VENV
# - source $VENV/bin/activate
# - pip install openpyxl
#

import os
import re

import openpyxl

AUXDATA_DIR = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources/auxdata/3_0/LANDSAT8')
BIOPHYSICAL_VARIABLES = ['LAI', 'FAPAR', 'FCOVER']
FILENAME_TEMPLATE = 'Algo_LANDSAT8OLI_{variable}.xlsx'
CELL_RE = re.compile('([A-Z]+)([0-9]+)')

def letter_range(start, stop):
    return [ chr(c) for c in range(ord(start), ord(stop)) ]
    
def parse_cellid(cellid):
    match = CELL_RE.match(cellid)
    return (str(match.group(1)), int(match.group(2)))

def get_col(cellid):
    return parse_cellid(cellid)[0]
    
def get_row(cellid):
    return parse_cellid(cellid)[1]
    
def get_next_col(cellid):
    return chr( ord(parse_cellid(cellid)[0]) + 1 )

def get_next_row(cellid):
    return parse_cellid(cellid)[1] + 1

def get_cell_value(ws, col, row):
    cell_value = str(ws['%s%s' % (col, row)].value).strip(' ').split(' ')[0]
    if cell_value == 'None':
        return None
    return cell_value

def csv_row(ws, ul_cell, lr_cell, row):
    col_range = letter_range(get_col(ul_cell), get_next_col(lr_cell))
    
    # return None if the first cell is None
    if not get_cell_value(ws, col_range[0], row):
        return None
    
    return [ get_cell_value(ws, col, row) for col in col_range ]

def csv_rows(ws, ul_cell, lr_cell):
    row_range = range(get_row(ul_cell), get_next_row(lr_cell))
    
    if not csv_row(ws, ul_cell, lr_cell, row_range[0]):
        return None
    
    return '\n'.join([ ','.join(csv_row(ws, ul_cell, lr_cell,row)) for row in row_range ])

def extract_table_to_csv(ws, filename, ul_cell, lr_cell):
    text = csv_rows(ws, ul_cell, lr_cell)
    if text:
        with open(filename, "w") as f:
            f.write(text)
            f.write('\n')
    else:
        if os.path.exists(filename):
            os.remove(filename)

def process_variable(variable):
    target_dir = os.path.join(AUXDATA_DIR, variable)
    if not os.path.exists(target_dir):
        os.mkdir(target_dir)
    
    source_file = os.path.join(AUXDATA_DIR, FILENAME_TEMPLATE.format(variable=variable))
    wb = openpyxl.load_workbook(filename = source_file)
    
    # Normalisation
    ws = wb[u'Normalisation']
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Normalisation' % variable), 'B6', 'C13')
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Denormalisation' % variable), 'B20', 'C20')

    # Extreme cases
    ws = wb[u'Extreme Cases']
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_ExtremeCases' % variable), 'B10', 'D10')
    
    # Weights
    ws = wb[u'Weights']
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Weights_Layer1_Neurons' % variable), 'B6', 'I10')
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Weights_Layer1_Bias' % variable), 'B11', 'F11')
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Weights_Layer2_Neurons' % variable), 'B15', 'F15')
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_Weights_Layer2_Bias' % variable), 'B16', 'B16')
    
    # Test Cases
    ws = wb[u'Test_Cases']
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_TestCases' % variable), 'A2', 'L101')

    # Definition Domain
    ws = wb[u'Definition_Domain']
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_DefinitionDomain_MinMax' % variable), 'B4', 'F5')
    extract_table_to_csv(ws, os.path.join(target_dir, '%s_DefinitionDomain_Grid' % variable), 'A10', 'E893')

if __name__ == '__main__':
    for variable in BIOPHYSICAL_VARIABLES:
        process_variable(variable)
