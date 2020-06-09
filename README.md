# AngryBirds-Pidgeotto
Refer the paper attached Angry_Birds_Pidgeotto_final.doc.

Class name: Heuristic

• In class Heuristic there is a function named ‘target_pt’, which returns
the target point to shoot the pigs.
• In this function whole strategy is implemented.
• We are choosing the pigs and then retrieving the blocks surrounded
to a particular pig.

• For the structure we are considering three situations.

1. When there is only one pig left in the game, then target point
will be the pig’s center point.

2. This condition will only occur when there exist blocks left to the
pig. Considering pig’s left structure, and in this structure
dividing all the blocks according to its type. Like all the Ices are
in one list, Wood in another and Stone in the other one list.
- Then in all the lists finding nearest block to the pig. Means
in the Ice containing list there would be one block which is
nearest to the pig amongst all the blocks, same for the
wood and stone.
- So we will get total 3 blocks wood, ice and stone which
are nearest to the pig.
- After finding type wise nearest block, we will check the
active bird. So target block will be taken according to the
active bird on the sling because birds have different
priority to the blocks. Example Yellow bird can easily hit to
the wood. So giving priority to the blocks according to the
bird.
- If YellowBird is active and there is no wood in the left
structure then our next priority would be Ice and if Ice also
doesn’t exist then wood. 
- If BlueBird , then first priority is Ice , then wood and then
stone.
- If RedBird then we will find the nearest block among this
three blocks wood, stone and Ice. Same for the WhiteBird
and BlackBird.
- In above all the cases at last we will get one block which
will be our target point

3. If 1st and 2nd condition fails then this condition will be executed.
Means if there is more than one pigs exist and left of the pig
there is no structure than we will check support of the pig.
 We will find nearest support of the pig, means minimum
Y-axis distance of the supporting blocks of the pig.

4. If 3rd condition also fails that means pig is alone. There no
support of the pig and on the left side of the pig no blocks exist.

- In this situation target will be the pig.

• In all the cases we have given lowest trajectory, because with the
lowest trajectory we can make maximum damage of the structure and
we can get the maximum score out of it.

• If particular level fails then we have changed the strategy and that
strategy will be of Naïve agent. Randomly generating pigs and
targeting the pig.

• Total score till 21st level is : 718640 

