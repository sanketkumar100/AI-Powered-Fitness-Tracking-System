import React, {useState} from "react";
import {Box, Button, FormControl, InputLabel, MenuItem, Select, TextField} from "@mui/material";
import {addActivity} from "../services/api.js";



const ActivityForm = (onActivityAdded) => {

    const [activity, setActivity] = useState({
        type: "RUNNING", duration: '', caloriesBurned: '',
        additionalMetrics: {}
    })



    const handleSubmit = async (e) => {
        e.preventDefault();
        try
        {
            await addActivity(activity);
            onActivityAdded();
            setActivity({type: "RUNNING", duration: '', caloriesBurned: ''});
        }
        catch (error)
        {
            console.error(error);
        }
    }

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ mb: 4 }}>
            <FormControl fullWidth sx={{mb: 2}}>
                <InputLabel>Activity Type</InputLabel>
                <Select
                value={activity.type}
                onChange={(e) => setActivity({...activity, type: e.target.value})}>
                    <MenuItem value="RUNNING">Running</MenuItem>
                    <MenuItem value="WALKING">Walking</MenuItem>
                    <MenuItem value="CYCLING">Cycling</MenuItem>
                    <MenuItem value="SWIMMING">Swimming</MenuItem>
                    <MenuItem value="ROWING">Rowing</MenuItem>
                    <MenuItem value="HIKING">Hiking</MenuItem>
                    <MenuItem value="WEIGHTLIFTITNG">Weightlifting</MenuItem>
                    <MenuItem value="DEADLIFT">Deadlift</MenuItem>
                    <MenuItem value="SQUATS">Squats</MenuItem>
                    <MenuItem value="BENCHPRESS">Bench Press</MenuItem>
                    <MenuItem value="PULLUPS">Pull-ups</MenuItem>
                    <MenuItem value="PUSHUPS">Push-ups</MenuItem>
                    <MenuItem value="YOGA">Yoga</MenuItem>
                    <MenuItem value="PILATES">Pilates</MenuItem>
                    <MenuItem value="MEDITATION">Meditation</MenuItem>
                    <MenuItem value="FOOTBALL">Football</MenuItem>
                    <MenuItem value="CRICKET">Cricket</MenuItem>
                    <MenuItem value="BADMINTON">Badminton</MenuItem>
                    <MenuItem value="DANCING">Dancing</MenuItem>
                    <MenuItem value="ZUMBA">Zumba</MenuItem>
                    <MenuItem value="MARTIALARTS">Martial Arts</MenuItem>
                    <MenuItem value="OTHER">Other</MenuItem>

                </Select>
            </FormControl>
            <TextField fullWidth
                         label="Duration (Minutes)"
                         type='number'
                         sx={{mb: 2}}
                         value={activity.duration}
                         onChange={(e) => setActivity({...activity, duration: e.target.value})}/>

            <TextField fullWidth
                       label="Calories Burned"
                       type='number'
                       sx={{mb: 2}}
                       value={activity.caloriesBurned}
                       onChange={(e) => setActivity({...activity, caloriesBurned: e.target.value})}/>

            <Button type='submit' variant='contained'>
                Add Activity
            </Button>
        </Box>
    )
}
export default ActivityForm