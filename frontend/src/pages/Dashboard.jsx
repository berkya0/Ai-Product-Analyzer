import PageHeader from "../components/PageHeader";
import StateCards from "../components/StateCards";
import '@fontsource/montserrat';

function Dashboard() {
    return (
        <div className="font-[Montserrat] p-8 min-h-screen">
            <PageHeader />
            <div className="flex gap-16 mt-6">
                 <StateCards title="Toplam Analiz" value="12">
            </StateCards>
             <StateCards title="Başarılı" value="12">
            </StateCards>
             <StateCards title="Başarısız" value="12">
            </StateCards>
              <StateCards title="Başarısız" value="12">
            </StateCards>
            </div>
           
        </div>
    );
}

export default Dashboard;