package com.example.testfirebase.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testfirebase.Helper.DijkstraAlgorithm;
import com.example.testfirebase.GetterSetterClass.Edge_cal;
import com.example.testfirebase.GetterSetterClass.Graph;
import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Vertex_cal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class testActivity extends AppCompatActivity {
    private TextView txt1,txt2;
    private Button btn;
    private List<Vertex_cal> nodes;
    private List<Edge_cal> edges;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    List<Vertex_cal> vertex_cals = new ArrayList<>();
    Vertex_cal vertex_cal ;
    Edge_cal edge_cal;
    List<Edge_cal> edge_cals = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        btn = findViewById(R.id.btn_cal_path);
        //txt1.setText("longtitude:99.398150 X:"+Math.round(lon2x(99.398150)));
        //txt2.setText("lattitude:18.317297 Y:"+Math.round(lat2y(18.317297)));


        //nodes = new ArrayList<Vertex_cal>();
        //edges = new ArrayList<Edge_cal>();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Vertex");
       /* mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    vertex_cal  = new Vertex_cal(dataSnapshot1.child("vertex_id").getValue(String.class)
                            ,dataSnapshot1.child("vertex_name").getValue(String.class));
                    vertex_cals.add(vertex_cal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mRef = mDatabase.getReference("Edge");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                    edge_cal = new Edge_cal(
                            dataSnapshot2.child("edge_id").getValue(String.class),
                            vertex_cals.get(Integer.parseInt(dataSnapshot2.child("edge_source").getValue(String.class))),
                            vertex_cals.get(Integer.parseInt(dataSnapshot2.child("edge_destination").getValue(String.class))),
                            Integer.parseInt(dataSnapshot2.child("edge_distance").getValue(String.class))
                    );

                    edge_cals.add(edge_cal);


                    /*addLane_firebase(
                            dataSnapshot2.child("edge_id").getValue(String.class),
                            Integer.parseInt(dataSnapshot2.child("edge_source").getValue(String.class)),
                            Integer.parseInt(dataSnapshot2.child("edge_destination").getValue(String.class)),
                            Integer.parseInt(dataSnapshot2.child("edge_distance").getValue(String.class))
                    );*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*String[] toppings = {"พระนครศรีอยุธยา","กรุงเทพ","เชียงราย","เชียงใหม่","ลพบุรี","สระบุรี"};
        for (int i = 0; i < toppings.length; i++) {
            Vertex_cal location = new Vertex_cal("node_"+i, toppings[i]);
            nodes.add(location);
        }*/


        /*addLane("Edge_0", 0, 1, 85);
        addLane("Edge_1", 0, 4, 805);
        addLane("Edge_2", 0, 2, 8055);
        addLane("Edge_3", 1, 4, 70);
        addLane("Edge_4", 1, 3, 20);
        addLane("Edge_5", 4, 3, 40);
        addLane("Edge_6", 4, 5, 100);
        addLane("Edge_7", 4, 2, 30);
        addLane("Edge_8", 3, 5, 30);
        addLane("Edge_9", 2, 5, 10);*/
        // Lets check from location Loc_1 to Loc_10


        /*Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<Vertex_cal> path = dijkstra.getPath(nodes.get(2));*/

       // assertNotNull(path);
       // assertTrue(path.size() > 0);
        /*int cal = 0;
        for(int i =0;i<path.size();i++){
            if(path.size() == i+1){
                break;
            }
            else{
                for (int j=0;j<edges.size();j++){
                    if(path.get(i).getVertex_name() == edges.get(j).getEdge_cal_source().getVertex_name() &&
                            path.get(i+1).getVertex_name() == edges.get(j).getEdge_cal_destination().getVertex_name()
                    ){
                        cal+=edges.get(j).getWeight();
                        System.out.println(
                                path.get(i).getVertex_id()+" "+edges.get(j).getEdge_cal_source().getVertex_name()
                                        +" "+path.get(i+1).getVertex_id()+" "+edges.get(j).getEdge_cal_destination().getVertex_name()
                                        +" distance "+edges.get(j).getWeight()
                        );
                    }
                }
            }

        }
        System.out.println(cal);*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*for(int i=0;i< vertex_cals.size();i++){
                    Log.d("tag",vertex_cals.get(i).getVertex_name());
                }
                Log.d("tag","-----------------------------------");
                for(int j=0;j< edge_cals.size();j++){
                    Log.d("tag",edge_cals.get(j).getEdge_cal_source()+"");
                }
                Log.d("tag","-----------------------------------");*/

                Graph graph = new Graph(vertex_cals, edge_cals); //init
                DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph); //init
                dijkstra.execute(vertex_cals.get(0)); // start
                LinkedList<Vertex_cal> path = dijkstra.getPath(vertex_cals.get(5)); //stop

                int cal = 0;
                for(int i =0;i<path.size();i++){
                    if(path.size() == i+1){
                        break;
                    }
                    else{
                        for (int j=0;j<edge_cals.size();j++){
                            if(path.get(i).getVertex_name() == edge_cals.get(j).getEdge_cal_source().getVertex_name() &&
                                    path.get(i+1).getVertex_name() == edge_cals.get(j).getEdge_cal_destination().getVertex_name()
                            ){
                                cal+=edge_cals.get(j).getWeight();
                                Log.d("taf",
                                        "From "+
                                        edge_cals.get(j).getEdge_cal_source().getVertex_name()
                                                +" To "+edge_cals.get(j).getEdge_cal_destination().getVertex_name()
                                                +" distance "+edge_cals.get(j).getWeight()
                                );
                            }
                        }
                    }

                }
                System.out.println(cal);


            }
        });
    }
    /*private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        Edge_cal lane = new Edge_cal(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
        edges.add(lane);
    }*/

    private void addLane_firebase(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        Edge_cal lane = new Edge_cal(laneId,vertex_cals.get(sourceLocNo), vertex_cals.get(destLocNo), duration );
        edge_cals.add(lane);
    }


    public static final double lat2y(double aLat)
    {
        return ((1 - Math.log(Math.tan(aLat * Math.PI / 180) + 1 / Math.cos(aLat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, 0)) * 256;
    }
    public static final double lon2x(double lon)
    {
        return (lon + 180f) / 360f * 256f;
    }



}
