package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryResponse extends PagedResponse{

    private List<Status> story;

    GetStoryResponse(String message) {
        super(false, message,false);
    }

    public GetStoryResponse(List<Status> story, boolean hasMorePages, AuthToken authToken) {
        super(true, hasMorePages, authToken);
        this.story = story;
    }

    public List<Status> getStory() {
        return story;
    }

    @Override
    public int hashCode() {
        return Objects.hash(story);
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        GetStoryResponse that = (GetStoryResponse) param;

        return (Objects.equals(story, that.story) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }


}
