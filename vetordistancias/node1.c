#include <stdio.h>

extern struct rtpkt {
  int sourceid;       /* id of sending router sending this pkt */
  int destid;         /* id of router to which pkt being sent 
                         (must be an immediate neighbor) */
  int mincost[4];    /* min cost to node 0 ... 3 */
  };

extern int TRACE;
extern int YES;
extern int NO;

int connectcosts1[4] = { 1,  0,  1, 999 };

struct distance_table 
{
  int costs[4][4];
} dt1;


/* students to write the following two routines, and maybe some others */


rtinit1() 
{
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	extern float clocktime;
	
	printf("inside rtinit1\n");
	printf("rtinit1: %f\n", clocktime);
	int i;
	int j;
	
	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			dt1.costs[i][j]= 999;
		}
	}
	
	dt1.costs[0][0]=1;
	dt1.costs[2][2]=1;
	
	for (i=0; i<4; i++) {
		for(j=0; j<4; j++) {
			if (packet.mincost[i] > dt1.costs[i][j])
				packet.mincost[i] = dt1.costs[i][j];
		}
	}
	
	packet.sourceid = 1;
	packet.destid = 0;
	tolayer2(packet);
	
	packet.sourceid = 1;
	packet.destid = 2;
	tolayer2(packet);
	
	packet.sourceid = 1;
	printdt1(&dt1);
	printf("done with rtinit1\n");
}


rtupdate1(rcvdpkt)
  struct rtpkt *rcvdpkt;
{
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	printf("inside rtupdate1\n");
	
	extern float clocktime;
	printf("rtupdate1: %f\n sender: %d\n", clocktime, rcvdpkt->sourceid);
	printdt1(&dt1);
	
	int temp[4];
	int change = 0;
	int i;
	int j;
	
	for (i = 0; i < 4; ++i) {
		if (dt1.costs[i][rcvdpkt->sourceid] > rcvdpkt->mincost[i] + dt1.costs[rcvdpkt->sourceid][rcvdpkt->sourceid]) {
			dt1.costs[i][rcvdpkt->sourceid] = rcvdpkt->mincost[i] + dt1.costs[rcvdpkt->sourceid][rcvdpkt->sourceid]; 
			change = 1;
		}
	}
	
	if (change) {
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				if (packet.mincost[i] > dt1.costs[i][j])
					packet.mincost[i] = dt1.costs[i][j];
			}
		}
		
		printf ("finished updating distance table\n");
		
		// simulating data link layer with new packets after table is updated
		packet.sourceid = 1;
		packet.destid = 0;
		tolayer2(packet);
		
		packet.sourceid = 1;
		packet.destid = 2;
		tolayer2(packet);
	}
	
	//pretty prints the distance table after update
	printdt1(&dt1);
}

printdt1(dtptr)
  struct distance_table *dtptr;  
{
  printf("             via   \n");
  printf("   D1 |    0     2 \n");
  printf("  ----|-----------\n");
  printf("     0|  %3d   %3d\n",dtptr->costs[0][0], dtptr->costs[0][2]);
  printf("dest 2|  %3d   %3d\n",dtptr->costs[2][0], dtptr->costs[2][2]);
  printf("     3|  %3d   %3d\n",dtptr->costs[3][0], dtptr->costs[3][2]);
}

linkhandler1(linkid, newcost)   
int linkid, newcost;   
/* called when cost from 1 to linkid changes from current value to newcost*/
/* You can leave this routine empty if you're an undergrad. If you want */
/* to use this routine, you'll need to change the value of the LINKCHANGE */
/* constant definition in prog3.c from 0 to 1 */	
{
}

