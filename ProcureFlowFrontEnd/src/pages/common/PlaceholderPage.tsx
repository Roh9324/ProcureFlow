interface PlaceholderPageProps {
  title: string;
  description: string;
}

function PlaceholderPage({ title, description }: PlaceholderPageProps) {
  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Coming Next</p>
          <h1>{title}</h1>
          <p>{description}</p>
        </div>
      </div>
      <div className="table-card">
        <div className="empty-state">This section will be added in a later backend milestone.</div>
      </div>
    </section>
  );
}

export default PlaceholderPage;
