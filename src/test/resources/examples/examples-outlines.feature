# Example Cucumber Scenario Outlines

@examples @all
Feature: Example Site Scenario Outlines
  
  @multi
  Scenario Outline: <scen_out_row_num>
    Given Step from '<scen_out_row_num>' in 'examples-outlines' feature file
    
    Examples:
      | scen_out_row_num        |
      | Scenario Outline Row 1  |
      | Scenario Outline Row 2  |
