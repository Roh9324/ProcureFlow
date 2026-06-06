import type { RequestHistoryItem } from '../../types/requestHistory';
import { formatDateTime } from '../../utils/dateUtils';
import StatusBadge from './StatusBadge';

interface RequestTimelineProps {
  items: RequestHistoryItem[];
}

function RequestTimeline({ items }: RequestTimelineProps) {
  if (items.length === 0) {
    return <div className="empty-state">No timeline entries found.</div>;
  }

  return (
    <div className="timeline">
      {items.map((item) => (
        <article className="timeline-item" key={item.id}>
          <div className="timeline-marker" />
          <div className="timeline-content">
            <div className="timeline-heading">
              <h2>{item.action}</h2>
              <span>{formatDateTime(item.changedAt)}</span>
            </div>
            <div className="timeline-status">
              {item.oldStatus ? <StatusBadge status={item.oldStatus} /> : <span>-</span>}
              <span>to</span>
              <StatusBadge status={item.newStatus} />
            </div>
            <p>{item.comment}</p>
            <small>
              {item.changedByName} ({item.changedByEmail})
            </small>
          </div>
        </article>
      ))}
    </div>
  );
}

export default RequestTimeline;
