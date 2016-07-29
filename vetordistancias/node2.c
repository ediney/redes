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

struct distance_table 
{
  int costs[4][4];
} dt2;

/* students to write the following two routines, and maybe some others */

struct rtpkt packet;

void rtinit2() 
{
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	extern float clocktime;
	
	printf("inside rtinit2\n");
	printf("rtinit2: %f\n", clocktime);
	int i;
	int j;
	
	for (i=0; i<4; i++) {
		for(j=0; j<4; j++) {
			dt2.costs[i][j]= 999;
		}
	}
	
	dt2.costs[0][0]=3;
	dt2.costs[1][1]=1;
	dt2.costs[2][2]=999;
	dt2.costs[3][3]=2;
	
	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			if (packet.mincost[i] > dt2.costs[i][j])
				packet.mincost[i] = dt2.costs[i][j];
		}
	}
	
	packet.sourceid = 2;
	packet.destid = 0;
	tolayer2(packet);
	
	packet.sourceid = 2;
	packet.destid = 1;
	tolayer2(packet);
	
	packet.sourceid = 2;
	packet.destid = 3;
	tolayer2(packet);
	
	printdt2(&dt2);
	printf("done with rtinit2\n");
}


void rtupdate2(rcvdpkt)
  struct rtpkt *rcvdpkt;  
{
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	printf ("inside rtupdate2\n");
	
	extern float clocktime;
	printf ("rtupdate2: %f\n sender: %d\n", clocktime, rcvdpkt->sourceid);
	printdt2(&dt2);
	
	int temp[4];
	int change = 0;
	int i;
	int j;
	
	for (i = 0; i < 4; ++i) {
		if (dt2.costs[i][rcvdpkt->sourceid] > rcvdpkt->mincost[i] + dt2.costs[rcvdpkt->sourceid][rcvdpkt->sourceid]) {
			dt2.costs[i][rcvdpkt->sourceid] = rcvdpkt->mincost[i] + dt2.costs[rcvdpkt->sourceid][rcvdpkt->sourceid];
			change = 1;
		}
	}
	
	if (change) {
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				if (packet.mincost[i] > dt2.costs[i][j])
					packet.mincost[i] = dt2.costs[i][j];
			}
		}
		
		printf("finished updating distance table\n");
		
		// simulating data link layer after update
		packet.sourceid = 2;
		packet.destid = 0;
		tolayer2(packet);
		
		packet.sourceid = 2;
		packet.destid = 1;
		tolayer2(packet);
		
		packet.sourceid = 2;
		packet.destid = 3;
		tolayer2(packet);
	}
	
	printdt2(&dt2);
}

printdt2(dtptr)
  struct distance_table *dtptr;  
{
  printf("                via     \n");
  printf("   D2 |    0     1    3 \n");
  printf("  ----|-----------------\n");
  printf("     0|  %3d   %3d   %3d\n",dtptr->costs[0][0],
	 dtptr->costs[0][1],dtptr->costs[0][3]);
  printf("dest 1|  %3d   %3d   %3d\n",dtptr->costs[1][0],
	 dtptr->costs[1][1],dtptr->costs[1][3]);
  printf("     3|  %3d   %3d   %3d\n",dtptr->costs[3][0],
	 dtptr->costs[3][1],dtptr->costs[3][3]);
}

linkhandler2(linkid, newcost)
  int linkid, newcost;

/* called when cost from 0 to linkid changes from current value to newcost*/
/* You can leave this routine empty if you're an undergrad. If you want */
/* to use this routine, you'll need to change the value of the LINKCHANGE */
/* constant definition in prog3.c from 0 to 1 */
{
}