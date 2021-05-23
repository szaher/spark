
@Name("Create Pickup") 
@Description("Create Pickup Event")
create schema Pickup(taxiId int, pickupLocation  int);

@Name("Create Dropoff")  
@Description("Create Dropoff Event")
create schema Dropoff(taxiId int, dropoffLocation int, amount int);


@Name("Create ComplexEvent schema")  
@Description("Create Complex Event that makes full completed trips")
create schema CompleteTripEvent(taxiId int, pickupLocation int, dropoffLocation int, amount int);

@Name("Create Complex Event")  
@Description("Add completed trips to CompleteTripEvent")  

insert into CompleteTripEvent
select a.taxiId as taxiId, 
a.pickupLocation as pickupLocation,
b.dropoffLocation  as dropoffLocation,
b.amount as amount
from pattern [every a=Pickup() -> b=Dropoff(taxiId=a.taxiId, a.pickupLocation != b.dropoffLocation )];


@Name("Ten Least Profitable Routes") 
@Description("Find the least 10 profitable routes")  
select sum(amount) as profit, pickupLocation, dropoffLocation from CompleteTripEvent.win:time_batch(40 min) group by pickupLocation, dropoffLocation order by profit asc limit 10;