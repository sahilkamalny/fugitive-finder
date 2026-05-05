package scratch;

import org.example.fugitivefinder.service.LeaderboardService;
import org.example.fugitivefinder.service.RegionStatsService;
import java.util.List;
import java.util.Map;

public class TestLeaderboard {
    public static void main(String[] args) {
        try {
            LeaderboardService ls = new LeaderboardService();
            System.out.println("Fetching most dangerous...");
            List<LeaderboardService.RankedFugitive> top = ls.getMostDangerous(20);
            System.out.println("Found: " + top.size());
            
            RegionStatsService rs = new RegionStatsService();
            System.out.println("Fetching region stats...");
            Map<String, RegionStatsService.RegionStat> stats = rs.getRegionStats();
            System.out.println("Stats size: " + stats.size());
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
