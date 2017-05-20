    package odata.service.mechine_learning;  
      
    /** 
     * Created by ehang on 2017/2/5. 
     */  
      
    import com.google.common.base.Throwables;

    import java.util.ArrayList;
import org.grouplens.lenskit.data.text.Formats;
import org.grouplens.lenskit.data.text.TextEventDAO;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;
import org.lenskit.LenskitConfiguration;  
    import org.lenskit.LenskitRecommender;  
    import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.RatingPredictor;
import org.lenskit.api.Result;  
    import org.lenskit.api.ResultList;
import org.lenskit.baseline.BaselineScorer;
import org.lenskit.baseline.ItemMeanRatingItemScorer;
import org.lenskit.baseline.UserMeanBaseline;
import org.lenskit.baseline.UserMeanItemScorer;
import org.lenskit.config.ConfigHelpers;
import org.lenskit.data.dao.EventDAO;
import org.lenskit.data.dao.ItemNameDAO;
import org.lenskit.data.dao.MapItemNameDAO;
import org.lenskit.knn.item.ItemItemScorer;
import org.slf4j.Logger;  
    import org.slf4j.LoggerFactory;  
       
    import java.io.File;  
    import java.io.IOException;  
    import java.nio.file.Path;  
    import java.nio.file.Paths;  
    import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;  
      
    public class test {   
      
        public static void main(String []args){  
        	try {
				new test().run();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	// Use item-item CF to score items  
        	LenskitConfiguration config = new LenskitConfiguration();
        	config.bind(ItemScorer.class)  
        	      .to(ItemItemScorer.class);  
        	// let's use personalized mean rating as the baseline/fallback predictor  
        	// 2-step process:  
        	// First, use the user mean rating as the baseline scorer  
        	config.bind(BaselineScorer.class, ItemScorer.class)  
        	      .to(UserMeanItemScorer.class);  
        	// Second, use the item mean rating as the base for user means  
        	config.bind(UserMeanBaseline.class, ItemScorer.class)  
        	      .to(ItemMeanRatingItemScorer.class);  
        	// and normalize ratings by baseline prior to computing similarities  
        	config.bind(UserVectorNormalizer.class)  
        	      .to(BaselineSubtractingUserVectorNormalizer.class);  
        	
        	
        	LenskitRecommender rec
        	 = LenskitRecommender.build(config);
        	
        	RatingPredictor
        	 pred = rec.getRatingPredictor();
        	
        	Result
        	 score = pred.predict(42,
        	17);
        }  
      
    	// about 100,000 pieces
    	private File inputFile = new File("data/ratings.csv");
    	// about 9,000 pieces
    	private File movieFile = new File("data/movies.csv");
    	private List<Long> users = new ArrayList<Long>(Arrays.asList(1L, 2L));

    	public void run() throws RuntimeException, IOException {
    		// We first need to configure the data access.
    		// We will use a simple delimited file; you can use something else like
    		// a database (see JDBCRatingDAO).
    		EventDAO dao = TextEventDAO
    				.create(inputFile, Formats.movieLensLatest());
    		ItemNameDAO names = MapItemNameDAO.fromCSVFile(movieFile, 1);

    		// Next: load the LensKit algorithm configuration
    		LenskitConfiguration config = ConfigHelpers.load(new File(
    				"etc/item-item.groovy"));
    		// Add our data component to the configuration
    		config.addComponent(dao);

    		// There are more parameters, roles, and components that can be set. See
    		// the
    		// JavaDoc for each recommender algorithm for more information.

    		// Now that we have a configuration, build a recommender engine from the
    		// configuration
    		// and data source. This will compute the similarity matrix and return a
    		// recommender
    		// engine that uses it.
    		LenskitRecommenderEngine engine = LenskitRecommenderEngine
    				.build(config);

    		// Finally, get the recommender and use it.
    		LenskitRecommender rec = engine.createRecommender();
    		// we want to recommend items
    		ItemRecommender itemRec = rec.getItemRecommender();
    		// for users
    		for (long user : users) {
    			// get 10 recommendation for the user
    			ResultList recs = itemRec
    					.recommendWithDetails(user, 10, null, null);
    			System.out.format("Recommendations for user %d:\n", user);
    			for (Result item : recs)
    				System.out.format("\t%d (%s): %.2f\n", item.getId(),
    						names.getItemName(item.getId()), item.getScore());
    		}// for-user
    	}

    }  