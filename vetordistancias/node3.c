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
} dt3;

/* students to write the following two routines, and maybe some others */

struct rtpkt packet;

void rtinit3() 
{
	extern float clocktime;
	
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	printf("inside rtinit3\n");
	printf("rtinit3: %f\n", clocktime);
	int i;
	int j;
	
	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			dt3.costs[i][j]= 999;
		}
	}
	
	dt3.costs[0][0]=7;
	dt3.costs[2][2]=2;
	dt3.costs[3][3]=999;
	
	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			if (packet.mincost[i] > dt3.costs[i][j])
				packet.mincost[i] = dt3.costs[i][j];
		}
	}
	
	packet.sourceid = 3;
	packet.destid = 0;
	tolayer2(packet);
	
	packet.sourceid = 3;
	packet.destid = 2;
	tolayer2(packet);
	
	packet.sourceid = 3;
	printdt3(&dt3);
	printf("done with rtinit3\n");
}


void rtupdate3(rcvdpkt)
  struct rtpkt *rcvdpkt;  
{
	packet.mincost[0] = 999;
	packet.mincost[1] = 999;
	packet.mincost[2] = 999;
	packet.mincost[3] = 999;
	
	printf ("inside rtupdate3\n");
	
	extern float clocktime;
	printf ("rtupdate3: %f\n sender: %d\n", clocktime, rcvdpkt->sourceid);
	printdt3(&dt3);
	
	int change = 0;
	int i;
	int j;
	
	for (i = 0; i < 4; ++i) {
		if (dt3.costs[i][rcvdpkt->sourceid] > rcvdpkt->mincost[i] + dt3.costs[rcvdpkt->sourceid][rcvdpkt->sourceid]) {
			dt3.costs[i][rcvdpkt->sourceid] = rcvdpkt->mincost[i] + dt3.costs[rcvdpkt->sourceid][rcvdpkt->sourceid];
			change = 1;
		}
	}
	
	if (change) {
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				if (packet.mincost[i] > dt3.costs[i][j])
					packet.mincost[i] = dt3.costs[i][j];
			}
		}
		
		printf ("finished updating distance table\n");
		
		//sends out data to data link layer after table has been updated
		packet.sourceid = 3;
		packet.destid = 0;
		tolayer2(packet);
		
		packet.sourceid = 3;
		packet.destid = 2;
		tolayer2(packet);
	}
	
	//pretty prints the table after all changes are made
	printdt3(&dt3);
}


printdt3(dtptr)
  struct distance_table *dtptr;  
{
  printf("             via     \n");
  printf("   D3 |    0     2 \n");
  printf("  ----|-----------\n");
  printf("     0|  %3d   %3d\n",dtptr->costs[0][0], dtptr->costs[0][2]);
  printf("dest 1|  %3d   %3d\n",dtptr->costs[1][0], dtptr->costs[1][2]);
  printf("     2|  %3d   %3d\n",dtptr->costs[2][0], dtptr->costs[2][2]);

}

