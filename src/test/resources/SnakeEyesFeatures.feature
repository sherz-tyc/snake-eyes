Feature: Snake Eyes Gameplay
	Players can submit an attempt with a fixed stake. Provided that the player has sufficient balance, game server will return an outcome.
	
	Scenario: Player submits an attempt and hit Snake Eyes (pair of value 1)
		Given Player with balance of 500 units submits an attempt with stake of 2 units
		When Outcome is a pair of 1
		Then Player balance is increased by 60 units, which is 558 (500 - 2 + (2 * 30))
		
	Scenario: Player submits an attempt and hit a pair that is not value of 1
		Given Player with balance of 500 units submits an attempt with stake of 2 units
		When Outcome is a pair of 3
		Then Player balance is increased by 14 units, which is 512 (500 - 2 + (2 * 7))
		
	Scenario: Player submits an attempt and did not hit a pair
		Given Player with balance of 500 units submits an attempt with stake of 2 units
		When Outcome is 2 and 5
		Then Player balance is decreased by 2 units, which is 498 (500 - 2)
		
	Scenario: Player exhausts his balance with an attempt and did not hit a pair
		Given Player with balance of 10 units submits an attempt with stake of 10 units
		When Outcome is 2 and 5
		Then Player balance is decreased by 10 units, which is 0
		
	Scenario: Player submits an attempt but did not have enough balance
		Given Player with balance of 5 units 
		When Player submit an attempt with stake of 10 units
		Then Attempt is denied
		AND Balance remains at 5 units
	
	Scenario: New player submits an attempt and did not hit a pair
		Given New Player submit attempt with stake of 10
		When Outcome is 3 and 1
		Then New Player balance is first initialised with 1000 units, then decreased by 10 units, which is 990
		
	Scenario: New player submits an attempt and hit Snake Eyes (pair of value 1)
		Given New Player submit attempt with stake of 10
		When Outcome is 1 and 1
		Then New Player balance is first initialised with 1000 units, then increased by 300 units, which is 1290 (1000 - 10 + (10 * 30))