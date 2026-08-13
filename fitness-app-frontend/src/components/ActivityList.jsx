import React, {useEffect, useState} from "react";
import {Card, CardContent, Grid, Typography} from "@mui/material";
import {useNavigate} from "react-router";
import {getActivities} from "../services/api.js";

const ActivityList = () => {

    const [activities, setActivities] = useState([]);
    const navigate = useNavigate();

    const fetchActivities = async () => {
        try {
            const respose = await getActivities();
            setActivities(respose.data);

        }catch (error)
        {
            console.error(error);
        }
    };
    useEffect(() => {
        fetchActivities()
    }, []);

    return (
        <Grid container spacing={2}>
            {activities.map((activity) => (
                <Grid container spacing={{ xs: 2, md: 3 }} columns={{ xs: 4, sm: 8, md: 12 }}>
                    <Card sx={{cursor: 'pointer'}}
                    onClick={() => navigate(`/activities/${activity.id}`)}>
                        <CardContent>
                            <Typography variant='h6'>{activity.type}</Typography>
                            <Typography>Duration: {activity.duration}</Typography>
                            <Typography>Calories: {activity.caloriesBurned}</Typography>
                        </CardContent>

                    </Card>
                </Grid>
                ))}
        </Grid>
    )
}
export default ActivityList