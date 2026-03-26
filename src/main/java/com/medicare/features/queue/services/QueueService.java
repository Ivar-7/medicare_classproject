package com.medicare.features.queue.services;

import com.medicare.features.queue.dao.QueueDAO;
import com.medicare.models.Queue;
import com.medicare.models.Queue.Status;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QueueService {

    private final QueueDAO queueDAO = new QueueDAO();

    public List<Queue> getAllQueues() throws SQLException {
        return queueDAO.findAll();
    }

    public List<Queue> getQueuesByStatus(Status status) throws SQLException {
        return queueDAO.findByStatus(status);
    }

    public Optional<Queue> getQueueById(int queueId) throws SQLException {
        return queueDAO.findById(queueId);
    }

    public void createQueue(Queue queue) throws SQLException {
        queueDAO.save(queue);
    }

    public void updateQueue(Queue queue) throws SQLException {
        queueDAO.update(queue);
    }

    public void deleteQueue(int queueId) throws SQLException {
        queueDAO.delete(queueId);
    }

    public int countQueues() throws SQLException {
        return queueDAO.count();
    }

    public int countByStatus(Status status) throws SQLException {
        return queueDAO.countByStatus(status);
    }
}
