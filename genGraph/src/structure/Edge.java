package structure;

public class Edge {
        //该边的头节点
        Event from;
        //该边的尾节点
        Event to;

        public Edge(Event from,Event to){
                this.from = from;
                this.to = to;
        }
        public Event getFrom() {
                return from;
        }

        public void setFrom(Event from) {
                this.from = from;
        }

        public Event getTo() {
                return to;
        }

        public void setTo(Event to) { this.to = to; }
}
